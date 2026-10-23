package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Feedback;
import com.campus.smartcampus.entity.Registration;
import com.campus.smartcampus.repository.EventRepository;
import com.campus.smartcampus.repository.FeedbackRepository;
import com.campus.smartcampus.repository.RegistrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    private Event event1;
    private Event event2;

    @BeforeEach
    void setUp() {
        event1 = new Event();
        event1.setId(1L);
        event1.setTitle("AI Workshop");
        event1.setCapacity(50);
        event1.setDate(LocalDate.now().plusDays(2));
        event1.setTime(LocalTime.of(10, 0));

        event2 = new Event();
        event2.setId(2L);
        event2.setTitle("Past Seminar");
        event2.setCapacity(30);
        event2.setDate(LocalDate.now().minusDays(3));
        event2.setTime(LocalTime.of(14, 0));
    }

    @Test
    void testGetDashboardMetrics() {
        Registration r1 = new Registration();
        r1.setId(10L);
        r1.setEventId(1L);
        r1.setStudentName("Alice");
        r1.setDepartment("Computer Science");
        r1.setStatus("CONFIRMED");
        r1.setTickets(1);
        r1.setPaymentAmount(150.0);

        Registration r2 = new Registration();
        r2.setId(11L);
        r2.setEventId(1L);
        r2.setStudentName("Bob");
        r2.setDepartment("Information Technology");
        r2.setStatus("CHECKED_IN");
        r2.setTickets(1);
        r2.setPaymentAmount(0.0);

        Registration r3 = new Registration();
        r3.setId(12L);
        r3.setEventId(1L);
        r3.setStudentName("Charlie");
        r3.setDepartment("Computer Science");
        r3.setStatus("WAITLISTED");

        Feedback f1 = new Feedback(1L, "Alice", "alice@campus.edu", 5, "Amazing hands-on technical workshop! Highly insightful.");
        Feedback f2 = new Feedback(1L, "Bob", "bob@campus.edu", 2, "Poor sound system and crowded room.");
        when(feedbackRepository.findAll()).thenReturn(List.of(f1, f2));

        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));
        when(registrationRepository.findAll()).thenReturn(List.of(r1, r2, r3));
        when(registrationRepository.countByEventIdAndStatus(1L, "CONFIRMED")).thenReturn(1L);
        when(registrationRepository.countByEventIdAndStatus(1L, "CHECKED_IN")).thenReturn(1L);
        when(registrationRepository.countByEventIdAndStatus(2L, "CONFIRMED")).thenReturn(0L);
        when(registrationRepository.countByEventIdAndStatus(2L, "CHECKED_IN")).thenReturn(0L);

        Map<String, Object> metrics = analyticsService.getDashboardMetrics();

        assertNotNull(metrics);
        assertEquals(2L, metrics.get("totalEvents"));
        assertEquals(1L, metrics.get("upcomingEvents"));
        assertEquals(1L, metrics.get("pastEvents"));
        assertEquals(3, metrics.get("totalRegistrations"));
        assertEquals(1L, metrics.get("confirmedCount"));
        assertEquals(1L, metrics.get("checkedInCount"));
        assertEquals(1L, metrics.get("waitlistCount"));
        assertEquals(50.0, (Double) metrics.get("turnoutPercentage"), 0.1);
        assertEquals(150.0, (Double) metrics.get("totalRevenue"), 0.01);
        assertEquals(1L, metrics.get("positiveFeedbackCount"));
        assertEquals(1L, metrics.get("criticalFeedbackCount"));
        assertEquals(2L, metrics.get("totalFeedbackCount"));
        assertNotNull(metrics.get("topKeywords"));
        assertNotNull(metrics.get("eventTypeDistribution"));
        assertNotNull(metrics.get("registrationVelocity"));

        @SuppressWarnings("unchecked")
        Map<String, Long> deptMap = (Map<String, Long>) metrics.get("departmentRegistrations");
        assertEquals(2L, deptMap.get("Computer Science"));
        assertEquals(1L, deptMap.get("Information Technology"));
    }

    @Test
    void testExtractTopKeywords() {
        Feedback f1 = new Feedback(1L, "Alice", "alice@campus.edu", 5, "The hands-on workshop had practical coding examples and great speaker delivery.");
        Feedback f2 = new Feedback(1L, "Bob", "bob@campus.edu", 4, "Hands-on exercises were great, very practical.");
        Feedback f3 = new Feedback(1L, "Charlie", "charlie@campus.edu", 3, "Decent speaker, average pacing.");

        Map<String, Integer> keywords = analyticsService.extractTopKeywords(List.of(f1, f2, f3));

        assertNotNull(keywords);
        assertTrue(keywords.containsKey("hands-on"));
        assertTrue(keywords.containsKey("practical") || keywords.containsKey("speaker"));
        assertEquals(2, keywords.get("hands-on"));
    }
}
