package com.campus.smartcampus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // Password encryption
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Multi-role users
    @Bean
    public InMemoryUserDetailsManager userDetailsService(
            PasswordEncoder passwordEncoder) {

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN", "SUPER_ADMIN")
                .build();

        UserDetails coordinator = User.builder()
                .username("coordinator")
                .password(passwordEncoder.encode("coord123"))
                .roles("COORDINATOR")
                .build();

        UserDetails cseCoordinator = User.builder()
                .username("cse.coordinator")
                .password(passwordEncoder.encode("coord123"))
                .roles("COORDINATOR")
                .build();

        UserDetails eceCoordinator = User.builder()
                .username("ece.coordinator")
                .password(passwordEncoder.encode("coord123"))
                .roles("COORDINATOR")
                .build();

        return new InMemoryUserDetailsManager(admin, coordinator, cseCoordinator, eceCoordinator);
    }

    // Security configuration
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            // Cookie-based CSRF token decouples CSRF from session commit on dynamic templates
            .csrf(csrf -> csrf
                .csrfTokenRepository(org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/api/**")
            )
            .httpBasic(org.springframework.security.config.Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth

                // Static resources & PWA assets
                .requestMatchers(
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/favicon.ico",
                    "/manifest.json",
                    "/sw.js",
                    "/icons/**"
                ).permitAll()

                // Public HTML pages
                .requestMatchers(
                    "/",
                    "/login",
                    "/events",
                    "/events/**",
                    "/registration/**",
                    "/registrations/**",
                    "/my-registrations",
                    "/my-registrations/**",
                    "/verify/certificate",
                    "/verify/certificate/**",
                    "/error",
                    "/error/**"
                ).permitAll()

                // Public REST APIs
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/events", "/api/events/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/events/*/questions").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/events/questions/*/upvote", "/api/events/*/questions/*/upvote").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/feedback/event/**").permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/feedback").permitAll()
                .requestMatchers("/api/public/certificates/verify/**").permitAll()

                // High-privilege admin actions require SUPER_ADMIN
                .requestMatchers(
                    "/admin/events/delete/**",
                    "/admin/analytics",
                    "/admin/notifications"
                ).hasRole("SUPER_ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/events/**").hasRole("SUPER_ADMIN")
                .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/feedback/**").hasRole("SUPER_ADMIN")

                // Protected REST APIs for Admin and Coordinators
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/events/*/questions/*/toggle-answered", "/api/events/questions/*/toggle-answered").hasAnyRole("ADMIN", "SUPER_ADMIN", "COORDINATOR")
                .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/events").hasAnyRole("ADMIN", "SUPER_ADMIN", "COORDINATOR")
                .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/events/**").hasAnyRole("ADMIN", "SUPER_ADMIN", "COORDINATOR")
                .requestMatchers("/api/registrations/**").hasAnyRole("ADMIN", "SUPER_ADMIN", "COORDINATOR")
                .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN", "COORDINATOR")

                // General admin dashboard and scanner accessible by ADMIN and COORDINATOR
                .requestMatchers("/admin/**")
                .hasAnyRole("ADMIN", "SUPER_ADMIN", "COORDINATOR")

                // Any other requests require authentication
                .anyRequest()
                .authenticated()
            )

            // Login configuration
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/events", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )

            // Logout configuration
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}