package com.campus.smartcampus.config;

import com.campus.smartcampus.entity.Event;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("campusSecurityService")
public class CampusSecurityService {

    private static final Map<String, CoordinatorProfile> COORDINATORS = new LinkedHashMap<>();

    static {
        COORDINATORS.put("admin", new CoordinatorProfile(
                "Chief Administrator",
                "admin@campus.edu",
                "admin",
                "All University Schools",
                "SUPER_ADMIN",
                "ACTIVE"
        ));
        COORDINATORS.put("cse.coordinator", new CoordinatorProfile(
                "Prof. S. Ramanathan",
                "cse.coordinator@campus.edu",
                "cse.coordinator",
                "Computer Science and Engineering",
                "COORDINATOR",
                "ACTIVE"
        ));
        COORDINATORS.put("ece.coordinator", new CoordinatorProfile(
                "Dr. Ananya Sen",
                "ece.coordinator@campus.edu",
                "ece.coordinator",
                "Electronics and Communication Engineering",
                "COORDINATOR",
                "ACTIVE"
        ));
        COORDINATORS.put("coordinator", new CoordinatorProfile(
                "Campus Coordinator",
                "coordinator@campus.edu",
                "coordinator",
                "Computer Science and Engineering",
                "COORDINATOR",
                "ACTIVE"
        ));
    }

    public boolean isAdmin(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN") || a.equals("ROLE_SUPER_ADMIN"));
    }

    public boolean isCoordinator(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_COORDINATOR"));
    }

    public String getUserDepartment(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        String username = auth.getName();
        CoordinatorProfile profile = COORDINATORS.get(username.toLowerCase());
        if (profile != null) {
            return profile.getDepartment();
        }
        return null;
    }

    public static String normalizeDepartment(String dept) {
        if (dept == null) return "";
        String clean = dept.trim().toLowerCase();
        if (clean.equals("cse") || clean.contains("computer science")) {
            return "computer science and engineering";
        }
        if (clean.equals("ece") || clean.contains("electronics and communication") || clean.contains("electronics & communication")) {
            return "electronics and communication engineering";
        }
        if (clean.equals("it") || clean.contains("information tech")) {
            return "information technology";
        }
        if (clean.equals("eee") || clean.contains("electrical and electronics") || clean.contains("electrical & electronics")) {
            return "electrical and electronics engineering";
        }
        if (clean.equals("mech") || clean.contains("mechanical")) {
            return "mechanical engineering";
        }
        if (clean.equals("civil")) {
            return "civil engineering";
        }
        if (clean.equals("ai & ds") || clean.equals("aids") || clean.contains("artificial intelligence")) {
            return "artificial intelligence and data science";
        }
        return clean;
    }

    public boolean canManageEvent(Authentication auth, Event event) {
        if (auth == null || !auth.isAuthenticated()) return false;
        if (isAdmin(auth)) return true;
        if (isCoordinator(auth)) {
            if (event == null) return true;
            String userDept = getUserDepartment(auth);
            if (userDept == null || event.getDepartment() == null) return false;
            return normalizeDepartment(userDept).equals(normalizeDepartment(event.getDepartment()));
        }
        return false;
    }

    public boolean canDeleteEvent(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return false;
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_SUPER_ADMIN"));
    }

    public List<CoordinatorProfile> getAllCoordinators() {
        return new ArrayList<>(COORDINATORS.values());
    }

    public static class CoordinatorProfile {
        private final String name;
        private final String email;
        private final String username;
        private final String department;
        private final String role;
        private final String status;

        public CoordinatorProfile(String name, String email, String username, String department, String role, String status) {
            this.name = name;
            this.email = email;
            this.username = username;
            this.department = department;
            this.role = role;
            this.status = status;
        }

        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getUsername() { return username; }
        public String getDepartment() { return department; }
        public String getRole() { return role; }
        public String getStatus() { return status; }
        public boolean isCanDelete() {
            return "SUPER_ADMIN".equalsIgnoreCase(role) || "ROLE_ADMIN".equalsIgnoreCase(role);
        }
    }
}