package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final FeedbackRepository feedbackRepository;
    private final QuestionRepository questionRepository;
    private final TeamRepository teamRepository;
    private final EventSessionRepository eventSessionRepository;
    private final CertificateRepository certificateRepository;

    @Autowired
    public EventService(
            EventRepository eventRepository,
            RegistrationRepository registrationRepository,
            FeedbackRepository feedbackRepository,
            @Autowired(required = false) QuestionRepository questionRepository,
            @Autowired(required = false) TeamRepository teamRepository,
            @Autowired(required = false) EventSessionRepository eventSessionRepository,
            @Autowired(required = false) CertificateRepository certificateRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.feedbackRepository = feedbackRepository;
        this.questionRepository = questionRepository;
        this.teamRepository = teamRepository;
        this.eventSessionRepository = eventSessionRepository;
        this.certificateRepository = certificateRepository;
    }

    public EventService(
            EventRepository eventRepository,
            RegistrationRepository registrationRepository,
            FeedbackRepository feedbackRepository) {
        this(eventRepository, registrationRepository, feedbackRepository, null, null, null, null);
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

        Long bookedTickets = registrationRepository.sumTicketsByEventId(id);
        long currentBooked = bookedTickets != null ? bookedTickets : 0L;
        if (updatedEvent.getCapacity() != null && updatedEvent.getCapacity() < currentBooked) {
            throw new IllegalArgumentException("Cannot reduce event capacity to " + updatedEvent.getCapacity() +
                    " because " + currentBooked + " tickets are already booked.");
        }

        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setDepartment(updatedEvent.getDepartment());
        existingEvent.setDate(updatedEvent.getDate());
        existingEvent.setTime(updatedEvent.getTime());
        existingEvent.setVenue(updatedEvent.getVenue());
        existingEvent.setType(updatedEvent.getType());
        existingEvent.setCapacity(updatedEvent.getCapacity());
        existingEvent.setPrice(updatedEvent.getPrice());
        existingEvent.setIsPaid(updatedEvent.getIsPaid());

        return eventRepository.save(existingEvent);
    }

    public void deleteEvent(Long id) {
        Event event = getEventById(id);

        if (questionRepository != null) {
            var questions = questionRepository.findByEventIdOrderByUpvotesDescCreatedAtDesc(id);
            if (!questions.isEmpty()) {
                questionRepository.deleteAll(questions);
            }
        }
        if (teamRepository != null) {
            var teams = teamRepository.findByEventId(id);
            if (!teams.isEmpty()) {
                teamRepository.deleteAll(teams);
            }
        }
        if (eventSessionRepository != null) {
            var sessions = eventSessionRepository.findByEventIdOrderBySessionOrderAscStartTimeAsc(id);
            if (!sessions.isEmpty()) {
                eventSessionRepository.deleteAll(sessions);
            }
        }
        var registrations = registrationRepository.findByEventId(id);
        if (!registrations.isEmpty()) {
            if (certificateRepository != null) {
                for (var r : registrations) {
                    certificateRepository.findByRegistrationId(r.getId()).ifPresent(certificateRepository::delete);
                }
            }
            registrationRepository.deleteAll(registrations);
        }
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

    public List<Event> getRelatedEvents(Long eventId) {
        Event current = getEventById(eventId);
        if (current == null) return List.of();
        List<Event> related = eventRepository.findRelatedEvents(
                current.getId(),
                current.getDepartment(),
                current.getType(),
                LocalDate.now()
        );
        enrichWithAvailability(related);
        if (related.size() > 3) {
            return related.subList(0, 3);
        }
        return related;
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
        if (events == null || events.isEmpty()) return;

        // Batch load ticket counts in 1 query
        Map<Long, Long> ticketCounts = new HashMap<>();
        try {
            List<Object[]> bookedList = registrationRepository.sumTicketsGroupedByEventId();
            for (Object[] row : bookedList) {
                Long evId = (Long) row[0];
                Long count = (Long) row[1];
                if (evId != null) {
                    ticketCounts.put(evId, count != null ? count : 0L);
                }
            }
        } catch (Exception ignored) {
        }

        // Batch load ratings and review counts in 1 query
        Map<Long, Double> avgRatings = new HashMap<>();
        Map<Long, Long> reviewCounts = new HashMap<>();
        try {
            List<Object[]> statsList = feedbackRepository.getRatingStatsGroupedByEventId();
            for (Object[] row : statsList) {
                Long evId = (Long) row[0];
                Double avg = (Double) row[1];
                Long count = (Long) row[2];
                if (evId != null) {
                    avgRatings.put(evId, avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
                    reviewCounts.put(evId, count != null ? count : 0L);
                }
            }
        } catch (Exception ignored) {
        }

        for (Event event : events) {
            if (event != null && event.getId() != null) {
                long booked = ticketCounts.getOrDefault(event.getId(), 0L);
                int available = Math.max(0, (int) (event.getCapacity() - booked));
                event.setAvailableSeats(available);
                event.setSoldOut(available <= 0);
                event.setAverageRating(avgRatings.getOrDefault(event.getId(), 0.0));
                event.setReviewCount(reviewCounts.getOrDefault(event.getId(), 0L));
            }
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