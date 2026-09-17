package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.repository.EventRepository;
import com.campus.smartcampus.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final com.campus.smartcampus.repository.FeedbackRepository feedbackRepository;

    public EventService(
            EventRepository eventRepository,
            RegistrationRepository registrationRepository,
            com.campus.smartcampus.repository.FeedbackRepository feedbackRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.feedbackRepository = feedbackRepository;
    }

    public List<Event> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        enrichWithAvailability(events);
        return events;
    }

    public List<Event> getUpcomingEvents() {
        List<Event> events = eventRepository.findByDateGreaterThanEqualOrderByDateAsc(
                LocalDate.now()
        );
        enrichWithAvailability(events);
        return events;
    }

    public Event getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Event not found with ID: " + id)
                );
        enrichWithAvailability(event);
        return event;
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event updatedEvent) {
        Event existingEvent = getEventById(id);

        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setDepartment(updatedEvent.getDepartment());
        existingEvent.setDate(updatedEvent.getDate());
        existingEvent.setTime(updatedEvent.getTime());
        existingEvent.setVenue(updatedEvent.getVenue());
        existingEvent.setType(updatedEvent.getType());
        existingEvent.setCapacity(updatedEvent.getCapacity());

        return eventRepository.save(existingEvent);
    }

    public void deleteEvent(Long id) {
        Event event = getEventById(id);
        // Also delete registrations for this event to maintain data integrity
        var registrations = registrationRepository.findByEventId(id);
        if (!registrations.isEmpty()) {
            registrationRepository.deleteAll(registrations);
        }
        // Also delete feedback for this event
        var feedbacks = feedbackRepository.findByEventIdOrderBySubmittedAtDesc(id);
        if (!feedbacks.isEmpty()) {
            feedbackRepository.deleteAll(feedbacks);
        }
        eventRepository.delete(event);
    }

    public List<Event> searchEvents(String keyword, String department, String type, LocalDate date) {
        List<Event> events = eventRepository.searchEvents(
                (keyword != null && !keyword.isBlank()) ? keyword.trim() : null,
                (department != null && !department.isBlank()) ? department.trim() : null,
                (type != null && !type.isBlank()) ? type.trim() : null,
                date
        );
        enrichWithAvailability(events);
        return events;
    }

    public List<Event> searchEvents(String keyword, String department, String type) {
        return searchEvents(keyword, department, type, null);
    }

    public List<Event> searchEvents(String department, String type) {
        return searchEvents(null, department, type, null);
    }

    public List<Event> searchEventsByFilters(LocalDate date, String department, String type) {
        return searchEvents(null, department, type, date);
    }

    public int getAvailableSeats(Long eventId, Integer totalCapacity) {
        if (eventId == null || totalCapacity == null) return 0;
        Long booked = registrationRepository.sumTicketsByEventId(eventId);
        return Math.max(0, (int) (totalCapacity - (booked != null ? booked : 0)));
    }

    public long getTotalEventsCount() {
        return eventRepository.count();
    }

    public long getUpcomingEventsCount() {
        return eventRepository.countByDateGreaterThanEqual(LocalDate.now());
    }

    public long getTotalRegistrationsCount() {
        return registrationRepository.countByStatus("CONFIRMED");
    }

    public long getTotalTicketsSold() {
        Long sum = registrationRepository.sumTotalTickets();
        return sum != null ? sum : 0L;
    }

    private void enrichWithAvailability(List<Event> events) {
        for (Event event : events) {
            enrichWithAvailability(event);
        }
    }

    private void enrichWithAvailability(Event event) {
        if (event != null && event.getId() != null) {
            int available = getAvailableSeats(event.getId(), event.getCapacity());
            event.setAvailableSeats(available);
            event.setSoldOut(available <= 0);

            // Enrich with rating and review statistics
            Double avgRating = feedbackRepository.getAverageRatingByEventId(event.getId());
            event.setAverageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0);
            event.setReviewCount(feedbackRepository.countByEventId(event.getId()));
        }
    }
}