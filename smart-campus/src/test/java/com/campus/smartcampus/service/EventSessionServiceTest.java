package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.EventSession;
import com.campus.smartcampus.repository.EventRepository;
import com.campus.smartcampus.repository.EventSessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventSessionServiceTest {

    @Mock
    private EventSessionRepository eventSessionRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventSessionService eventSessionService;

    @Test
    @DisplayName("getSessionsByEventId should return ordered sessions for event")
    void testGetSessionsByEventId() {
        Event event = new Event();
        event.setId(1L);
        EventSession s1 = new EventSession(event, "Keynote", LocalTime.of(9, 30), LocalTime.of(10, 30), "Dr. Vance", "Hall A", "KEYNOTE", 1);
        EventSession s2 = new EventSession(event, "Hands-on Lab", LocalTime.of(11, 0), LocalTime.of(12, 30), "Prof. Smith", "Lab 2", "WORKSHOP", 2);

        when(eventSessionRepository.findByEventIdOrderBySessionOrderAscStartTimeAsc(1L)).thenReturn(List.of(s1, s2));

        List<EventSession> result = eventSessionService.getSessionsByEventId(1L);
        assertEquals(2, result.size());
        assertEquals("Keynote", result.get(0).getSessionTitle());
        assertEquals("Hands-on Lab", result.get(1).getSessionTitle());
    }

    @Test
    @DisplayName("addSession should associate session with event and save")
    void testAddSession() {
        Event event = new Event();
        event.setId(5L);
        EventSession session = new EventSession();
        session.setSessionTitle("Panel Discussion");

        when(eventRepository.findById(5L)).thenReturn(Optional.of(event));
        when(eventSessionRepository.save(any(EventSession.class))).thenAnswer(inv -> inv.getArgument(0));

        EventSession saved = eventSessionService.addSession(5L, session);
        assertNotNull(saved);
        assertEquals(event, saved.getEvent());
        assertEquals("Panel Discussion", saved.getSessionTitle());
        verify(eventSessionRepository, times(1)).save(session);
    }
}
