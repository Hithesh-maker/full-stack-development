package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.EventSession;
import com.campus.smartcampus.repository.EventRepository;
import com.campus.smartcampus.repository.EventSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EventSessionService {

    private final EventSessionRepository eventSessionRepository;
    private final EventRepository eventRepository;

    public EventSessionService(EventSessionRepository eventSessionRepository, EventRepository eventRepository) {
        this.eventSessionRepository = eventSessionRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public List<EventSession> getSessionsByEventId(Long eventId) {
        return eventSessionRepository.findByEventIdOrderBySessionOrderAscStartTimeAsc(eventId);
    }

    public EventSession addSession(Long eventId, EventSession session) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));
        session.setEvent(event);
        return eventSessionRepository.save(session);
    }

    public void deleteSession(Long sessionId) {
        eventSessionRepository.deleteById(sessionId);
    }
}
