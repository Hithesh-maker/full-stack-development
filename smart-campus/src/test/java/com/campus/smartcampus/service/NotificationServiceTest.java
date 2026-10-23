package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.NotificationLog;
import com.campus.smartcampus.entity.Registration;
import com.campus.smartcampus.repository.NotificationLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Event testEvent;
    private Registration testReg;

    @BeforeEach
    void setUp() {
        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Cybersecurity Summit 2026");
        testEvent.setDate(LocalDate.now().plusDays(4));
        testEvent.setTime(LocalTime.of(11, 0));
        testEvent.setVenue("Auditorium B");

        testReg = new Registration();
        testReg.setId(50L);
        testReg.setEventId(1L);
        testReg.setStudentName("Tanvi Roy");
        testReg.setEmail("tanvi.r@campus.edu");
        testReg.setToken("SC-CS-1234");
        testReg.setEvent(testEvent);
    }

    @Test
    void testSendBookingConfirmation() {
        when(notificationLogRepository.save(any(NotificationLog.class))).thenAnswer(inv -> inv.getArgument(0));

        notificationService.sendBookingConfirmation(testReg);

        verify(notificationLogRepository, times(1)).save(argThat(log ->
                "tanvi.r@campus.edu".equals(log.getRecipientEmail()) &&
                "BOOKING_CONFIRMATION".equals(log.getEventType()) &&
                log.getSubject().contains("Confirmed") &&
                log.getBodyHtml().contains("Cybersecurity Summit 2026")
        ));
    }

    @Test
    void testSendCheckInWelcome() {
        when(notificationLogRepository.save(any(NotificationLog.class))).thenAnswer(inv -> inv.getArgument(0));

        notificationService.sendCheckInWelcome(testReg);

        verify(notificationLogRepository, times(1)).save(argThat(log ->
                "tanvi.r@campus.edu".equals(log.getRecipientEmail()) &&
                "CHECKIN_VERIFIED".equals(log.getEventType()) &&
                log.getSubject().toLowerCase().contains("welcome")
        ));
    }

    @Test
    void testSendWaitlistPromotionAlert() {
        when(notificationLogRepository.save(any(NotificationLog.class))).thenAnswer(inv -> inv.getArgument(0));

        notificationService.sendWaitlistPromotionAlert(testReg);

        verify(notificationLogRepository, times(1)).save(argThat(log ->
                "tanvi.r@campus.edu".equals(log.getRecipientEmail()) &&
                "WAITLIST_PROMOTION".equals(log.getEventType()) &&
                log.getSubject().toLowerCase().contains("promoted")
        ));
    }
}
