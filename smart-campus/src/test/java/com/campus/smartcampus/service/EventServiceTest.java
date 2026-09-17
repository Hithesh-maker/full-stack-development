package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.repository.EventRepository;
import com.campus.smartcampus.repository.RegistrationRepository;
import com.campus.smartcampus.repository.FeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;

    @BeforeEach
    void setUp() {
        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("AI Summit 2026");
        testEvent.setDescription("Annual AI and Machine Learning Summit");
        testEvent.setDepartment("Computer Science and Engineering");
        testEvent.setDate(LocalDate.now().plusDays(10));
        testEvent.setTime(LocalTime.of(10, 0));
        testEvent.setVenue("Auditorium");
        testEvent.setType("Seminar");
        testEvent.setCapacity(100);
    }

    @Test
    void testGetEventById_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(registrationRepository.sumTicketsByEventId(1L)).thenReturn(20L);

        Event event = eventService.getEventById(1L);

        assertNotNull(event);
        assertEquals("AI Summit 2026", event.getTitle());
        assertEquals(80, event.getAvailableSeats());
        assertFalse(event.isSoldOut());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    void testGetEventById_NotFound() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> eventService.getEventById(999L));
        assertTrue(exception.getMessage().contains("Event not found with ID: 999"));
    }

    @Test
    void testCreateEvent() {
        when(eventRepository.save(any(Event.class))).thenReturn(testEvent);

        Event created = eventService.createEvent(testEvent);

        assertNotNull(created);
        assertEquals("AI Summit 2026", created.getTitle());
        verify(eventRepository, times(1)).save(testEvent);
    }

    @Test
    void testSearchEvents() {
        when(eventRepository.searchEvents("AI", "Computer Science and Engineering", "Seminar", null))
                .thenReturn(List.of(testEvent));
        when(registrationRepository.sumTicketsByEventId(1L)).thenReturn(0L);

        List<Event> results = eventService.searchEvents("AI", "Computer Science and Engineering", "Seminar");

        assertEquals(1, results.size());
        assertEquals("AI Summit 2026", results.get(0).getTitle());
        assertEquals(100, results.get(0).getAvailableSeats());
    }

    @Test
    void testGetAvailableSeats_SoldOut() {
        when(registrationRepository.sumTicketsByEventId(1L)).thenReturn(100L);

        int available = eventService.getAvailableSeats(1L, 100);

        assertEquals(0, available);
    }

    @Test
    void testSearchEvents_WithDateFilter() {
        LocalDate eventDate = testEvent.getDate();
        when(eventRepository.searchEvents("AI", "Computer Science and Engineering", "Seminar", eventDate))
                .thenReturn(List.of(testEvent));
        when(registrationRepository.sumTicketsByEventId(1L)).thenReturn(10L);

        List<Event> results = eventService.searchEvents("AI", "Computer Science and Engineering", "Seminar", eventDate);

        assertEquals(1, results.size());
        assertEquals("AI Summit 2026", results.get(0).getTitle());
        assertEquals(90, results.get(0).getAvailableSeats());
    }
}
