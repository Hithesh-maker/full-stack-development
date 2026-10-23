package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Registration;
import com.campus.smartcampus.repository.EventRepository;
import com.campus.smartcampus.repository.RegistrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private RegistrationService registrationService;

    private Event upcomingEvent;
    private Event pastEvent;
    private Registration registration;

    @BeforeEach
    void setUp() {
        upcomingEvent = new Event();
        upcomingEvent.setId(1L);
        upcomingEvent.setTitle("Hackathon 2026");
        upcomingEvent.setDepartment("Computer Science and Engineering");
        upcomingEvent.setDate(LocalDate.now().plusDays(5));
        upcomingEvent.setTime(LocalTime.of(9, 0));
        upcomingEvent.setVenue("Tech Lab");
        upcomingEvent.setType("Hackathon");
        upcomingEvent.setCapacity(50);

        pastEvent = new Event();
        pastEvent.setId(2L);
        pastEvent.setTitle("Old Symposium");
        pastEvent.setDate(LocalDate.now().minusDays(5));
        pastEvent.setCapacity(50);

        registration = new Registration();
        registration.setEventId(1L);
        registration.setStudentName("John Doe");
        registration.setEmail("johndoe@campus.edu");
        registration.setDepartment("Computer Science and Engineering");
        registration.setTickets(2);
    }

    @Test
    void testRegisterStudent_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(upcomingEvent));
        when(registrationRepository.existsByEventIdAndEmailIgnoreCaseAndStatus(1L, "johndoe@campus.edu", "CONFIRMED"))
                .thenReturn(false);
        when(registrationRepository.sumTicketsByEventId(1L)).thenReturn(10L);
        when(registrationRepository.save(any(Registration.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Registration result = registrationService.registerStudent(registration);

        assertNotNull(result);
        assertEquals("CONFIRMED", result.getStatus());
        assertNotNull(result.getRegisteredAt());
        assertEquals(upcomingEvent, result.getEvent());
        verify(registrationRepository, times(1)).save(registration);
    }

    @Test
    void testRegisterStudent_PastEvent_ThrowsException() {
        registration.setEventId(2L);
        when(eventRepository.findById(2L)).thenReturn(Optional.of(pastEvent));

        Exception ex = assertThrows(RuntimeException.class, () -> registrationService.registerStudent(registration));
        assertTrue(ex.getMessage().contains("Cannot register for an event that has already ended"));
    }

    @Test
    void testRegisterStudent_DuplicateEmail_ThrowsException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(upcomingEvent));
        when(registrationRepository.existsByEventIdAndEmailIgnoreCaseAndStatus(1L, "johndoe@campus.edu", "CONFIRMED"))
                .thenReturn(true);

        Exception ex = assertThrows(RuntimeException.class, () -> registrationService.registerStudent(registration));
        assertTrue(ex.getMessage().contains("already registered"));
    }

    @Test
    void testRegisterStudent_ExceedsCapacity_ThrowsException() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(upcomingEvent));
        when(registrationRepository.existsByEventIdAndEmailIgnoreCaseAndStatus(1L, "johndoe@campus.edu", "CONFIRMED"))
                .thenReturn(false);
        // 49 already booked out of 50 capacity
        when(registrationRepository.sumTicketsByEventId(1L)).thenReturn(49L);

        Exception ex = assertThrows(RuntimeException.class, () -> registrationService.registerStudent(registration));
        assertTrue(ex.getMessage().contains("Only 1 seat is available"));
    }

    @Test
    void testCancelRegistration_Success() {
        registration.setId(101L);
        registration.setStatus("CONFIRMED");

        when(registrationRepository.findById(101L)).thenReturn(Optional.of(registration));
        when(registrationRepository.save(any(Registration.class))).thenReturn(registration);

        registrationService.cancelRegistration(101L, "johndoe@campus.edu");

        assertEquals("CANCELLED", registration.getStatus());
        verify(registrationRepository, times(1)).save(registration);
    }

    @Test
    void testGenerateCsvForEvent() {
        registration.setId(101L);
        registration.setStatus("CONFIRMED");

        when(eventRepository.findById(1L)).thenReturn(Optional.of(upcomingEvent));
        when(registrationRepository.findByEventId(1L)).thenReturn(java.util.List.of(registration));

        String csv = registrationService.generateCsvForEvent(1L);

        assertNotNull(csv);
        assertTrue(csv.contains("Student Name"));
        assertTrue(csv.contains("John Doe"));
        assertTrue(csv.contains("johndoe@campus.edu"));
        assertTrue(csv.contains("Hackathon 2026"));
    }

    @Test
    void testCheckInStudent_Success() {
        registration.setId(101L);
        registration.setToken("SC-E1-TESTTOKEN");
        registration.setStatus("CONFIRMED");

        when(registrationRepository.findByToken("SC-E1-TESTTOKEN")).thenReturn(Optional.of(registration));
        when(registrationRepository.save(any(Registration.class))).thenAnswer(i -> i.getArgument(0));

        Registration checkedIn = registrationService.checkInStudent("SC-E1-TESTTOKEN");

        assertNotNull(checkedIn);
        assertEquals("CHECKED_IN", checkedIn.getStatus());
        assertNotNull(checkedIn.getCheckedInAt());
        verify(registrationRepository, times(1)).save(checkedIn);
    }

    @Test
    void testCheckInStudent_AlreadyCheckedIn_ThrowsException() {
        registration.setId(101L);
        registration.setToken("SC-E1-TESTTOKEN");
        registration.setStatus("CHECKED_IN");
        registration.setCheckedInAt(java.time.LocalDateTime.now());

        when(registrationRepository.findByToken("SC-E1-TESTTOKEN")).thenReturn(Optional.of(registration));

        Exception ex = assertThrows(RuntimeException.class, () -> registrationService.checkInStudent("SC-E1-TESTTOKEN"));
        assertTrue(ex.getMessage().contains("Duplicate Entry"));
    }

    @Test
    void testCancelRegistration_PromotesWaitlistCandidate() {
        registration.setId(101L);
        registration.setEventId(1L);
        registration.setStatus("CONFIRMED");

        Registration waitlisted = new Registration();
        waitlisted.setId(202L);
        waitlisted.setEventId(1L);
        waitlisted.setStatus("WAITLISTED");
        waitlisted.setWaitlistPosition(1);

        when(registrationRepository.findById(101L)).thenReturn(Optional.of(registration));
        when(registrationRepository.save(any(Registration.class))).thenAnswer(i -> i.getArgument(0));
        when(registrationRepository.findFirstByEventIdAndStatusOrderByIdAsc(1L, "WAITLISTED"))
                .thenReturn(Optional.of(waitlisted));

        registrationService.cancelRegistration(101L, "johndoe@campus.edu");

        assertEquals("CANCELLED", registration.getStatus());
        assertEquals("CONFIRMED", waitlisted.getStatus());
        assertNull(waitlisted.getWaitlistPosition());
        assertNotNull(waitlisted.getToken());
    }

    @Test
    void testJoinWaitlist_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(upcomingEvent));
        when(registrationRepository.existsActiveRegistration(1L, "johndoe@campus.edu")).thenReturn(false);
        when(registrationRepository.countByEventIdAndStatus(1L, "WAITLISTED")).thenReturn(2L);
        when(registrationRepository.save(any(Registration.class))).thenAnswer(i -> i.getArgument(0));

        Registration result = registrationService.joinWaitlist(registration);

        assertNotNull(result);
        assertEquals("WAITLISTED", result.getStatus());
        assertEquals(3, result.getWaitlistPosition());
        assertEquals(1, result.getTickets());
    }

    @Test
    void testGetCheckedInHistory_All() {
        Registration r1 = new Registration();
        r1.setId(10L);
        r1.setStatus("CHECKED_IN");
        r1.setCheckedInAt(java.time.LocalDateTime.now());

        when(registrationRepository.findByStatusOrderByCheckedInAtDesc("CHECKED_IN"))
                .thenReturn(java.util.List.of(r1));

        var history = registrationService.getCheckedInHistory(null);

        assertEquals(1, history.size());
        assertEquals("CHECKED_IN", history.get(0).getStatus());
        verify(registrationRepository, times(1)).findByStatusOrderByCheckedInAtDesc("CHECKED_IN");
    }

    @Test
    void testGetCheckedInHistory_ByEvent() {
        Registration r1 = new Registration();
        r1.setId(11L);
        r1.setEventId(1L);
        r1.setStatus("CHECKED_IN");
        r1.setCheckedInAt(java.time.LocalDateTime.now());

        when(registrationRepository.findByEventIdAndStatusOrderByCheckedInAtDesc(1L, "CHECKED_IN"))
                .thenReturn(java.util.List.of(r1));

        var history = registrationService.getCheckedInHistory(1L);

        assertEquals(1, history.size());
        assertEquals(1L, history.get(0).getEventId());
        verify(registrationRepository, times(1)).findByEventIdAndStatusOrderByCheckedInAtDesc(1L, "CHECKED_IN");
    }

    @Test
    void testBulkRegisterFromCsv() {
        String csvContent = "Name,Email,Department\n" +
                "Kunal Sen,kunal.s@campus.edu,Computer Science\n" +
                "Priya Nair,priya.n@campus.edu,Information Technology\n";
        java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(csvContent.getBytes(java.nio.charset.StandardCharsets.UTF_8));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(upcomingEvent));
        when(registrationRepository.existsActiveRegistration(1L, "kunal.s@campus.edu")).thenReturn(false);
        when(registrationRepository.existsActiveRegistration(1L, "priya.n@campus.edu")).thenReturn(false);
        when(registrationRepository.save(any(Registration.class))).thenAnswer(i -> i.getArgument(0));

        RegistrationService.BulkUploadResult result = registrationService.bulkRegisterFromCsv(1L, in);

        assertNotNull(result);
        assertEquals(2, result.getSuccessCount());
        assertEquals(0, result.getDuplicateCount());
        assertEquals(0, result.getErrorCount());
    }
}
