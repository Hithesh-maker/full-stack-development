package com.cloud.notification.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class ApiKeySecurityFilter extends OncePerRequestFilter {

    @Value("${cloud.security.api-key:vibe-cloud-secret-key-2026}")
    private String configuredApiKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Allow actuator health checks for Kubernetes probes without API key
        if (path.startsWith("/actuator") || path.equals("/") || path.startsWith("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }

        String requestApiKey = request.getHeader("X-API-KEY");

        if (requestApiKey == null || !requestApiKey.equals(configuredApiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Invalid or missing X-API-KEY header.\", \"status\": 401}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
