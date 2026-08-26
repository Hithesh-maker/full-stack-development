package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getUpcomingEvents() {
        return eventRepository.findByDateGreaterThanEqual(
                LocalDate.now()
        );
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Event not found with ID: " + id
                        )
                );
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

        eventRepository.delete(event);
    }

    public List<Event> searchEvents(
            String department,
            String type) {

        if (department != null
                && !department.isBlank()
                && type != null
                && !type.isBlank()) {

            return eventRepository
                    .findByDepartmentIgnoreCaseAndTypeIgnoreCase(
                            department,
                            type
                    );
        }

        if (department != null && !department.isBlank()) {

            return eventRepository
                    .findByDepartmentIgnoreCase(department);
        }

        if (type != null && !type.isBlank()) {

            return eventRepository
                    .findByTypeIgnoreCase(type);
        }

        return getAllEvents();
    }
}