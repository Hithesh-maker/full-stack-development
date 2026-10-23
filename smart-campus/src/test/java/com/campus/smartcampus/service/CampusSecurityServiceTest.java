package com.campus.smartcampus.service;

import com.campus.smartcampus.config.CampusSecurityService;
import com.campus.smartcampus.entity.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CampusSecurityServiceTest {

    private CampusSecurityService securityService;
    private Event cseEvent;
    private Event eceEvent;

    @BeforeEach
    void setUp() {
        securityService = new CampusSecurityService();

        cseEvent = new Event();
        cseEvent.setId(101L);
        cseEvent.setTitle("CSE Hackathon");
        cseEvent.setDepartment("Computer Science and Engineering");

        eceEvent = new Event();
        eceEvent.setId(102L);
        eceEvent.setTitle("ECE IoT Expo");
        eceEvent.setDepartment("Electronics and Communication Engineering");
    }

    @Test
    void testAdminCanManageAllEvents() {
        Authentication adminAuth = new UsernamePasswordAuthenticationToken(
                "admin", "admin123",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"))
        );

        assertTrue(securityService.isAdmin(adminAuth));
        assertTrue(securityService.canManageEvent(adminAuth, cseEvent));
        assertTrue(securityService.canManageEvent(adminAuth, eceEvent));
        assertTrue(securityService.canDeleteEvent(adminAuth));
    }

    @Test
    void testCseCoordinatorCanManageOnlyCseEvents() {
        Authentication cseAuth = new UsernamePasswordAuthenticationToken(
                "cse.coordinator", "coord123",
                List.of(new SimpleGrantedAuthority("ROLE_COORDINATOR"))
        );

        assertTrue(securityService.isCoordinator(cseAuth));
        assertFalse(securityService.isAdmin(cseAuth));
        assertEquals("Computer Science and Engineering", securityService.getUserDepartment(cseAuth));

        // Same department -> ALLOWED
        assertTrue(securityService.canManageEvent(cseAuth, cseEvent));

        // Different department -> DENIED
        assertFalse(securityService.canManageEvent(cseAuth, eceEvent));

        // Coordinator CANNOT delete
        assertFalse(securityService.canDeleteEvent(cseAuth));
    }

    @Test
    void testEceCoordinatorCanManageOnlyEceEvents() {
        Authentication eceAuth = new UsernamePasswordAuthenticationToken(
                "ece.coordinator", "coord123",
                List.of(new SimpleGrantedAuthority("ROLE_COORDINATOR"))
        );

        assertTrue(securityService.isCoordinator(eceAuth));
        assertEquals("Electronics and Communication Engineering", securityService.getUserDepartment(eceAuth));

        // Same department -> ALLOWED
        assertTrue(securityService.canManageEvent(eceAuth, eceEvent));

        // Different department -> DENIED
        assertFalse(securityService.canManageEvent(eceAuth, cseEvent));

        // Coordinator CANNOT delete
        assertFalse(securityService.canDeleteEvent(eceAuth));
    }

    @Test
    void testNullOrUnauthenticatedAuthenticationDenied() {
        assertFalse(securityService.isAdmin(null));
        assertFalse(securityService.isCoordinator(null));
        assertFalse(securityService.canManageEvent(null, cseEvent));
        assertFalse(securityService.canDeleteEvent(null));
        assertNull(securityService.getUserDepartment(null));
    }

    @Test
    void testCoordinatorDirectoryList() {
        var coordinators = securityService.getAllCoordinators();
        assertNotNull(coordinators);
        assertTrue(coordinators.size() >= 3);
        assertTrue(coordinators.stream().anyMatch(c -> c.getUsername().equals("cse.coordinator")));
        assertTrue(coordinators.stream().anyMatch(c -> c.getUsername().equals("ece.coordinator")));
    }

    @Test
    void testDepartmentAcronymNormalization() {
        Authentication cseAuth = new UsernamePasswordAuthenticationToken(
                "cse.coordinator", "coord123",
                List.of(new SimpleGrantedAuthority("ROLE_COORDINATOR"))
        );

        Event eventWithAcronym = new Event();
        eventWithAcronym.setDepartment("cse");
        assertTrue(securityService.canManageEvent(cseAuth, eventWithAcronym));

        Event eventWithFullName = new Event();
        eventWithFullName.setDepartment("Computer Science and Engineering");
        assertTrue(securityService.canManageEvent(cseAuth, eventWithFullName));

        Event eventEce = new Event();
        eventEce.setDepartment("ECE");
        assertFalse(securityService.canManageEvent(cseAuth, eventEce));
    }
}