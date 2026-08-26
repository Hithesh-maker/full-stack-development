package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Registration;
import com.campus.smartcampus.repository.EventRepository;
import com.campus.smartcampus.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;

    public RegistrationService(
            RegistrationRepository registrationRepository,
            EventRepository eventRepository) {

        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
    }

    // Register a student for an event
    public Registration registerStudent(
            Registration registration) {

        Event event = eventRepository
                .findById(registration.getEventId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Event not found with ID: "
                                        + registration.getEventId()
                        ));

        if (registration.getTickets() == null
                || registration.getTickets() <= 0) {

            throw new RuntimeException(
                    "Number of tickets must be greater than zero"
            );
        }

        long registeredTickets =
                registrationRepository
                        .findByEventId(event.getId())
                        .stream()
                        .mapToLong(Registration::getTickets)
                        .sum();

        long availableSeats =
                event.getCapacity() - registeredTickets;

        if (registration.getTickets() > availableSeats) {

            throw new RuntimeException(
                    "Only " + availableSeats
                            + " seats are available"
            );
        }

        return registrationRepository.save(registration);
    }

    // Get all registrations
    public List<Registration> getAllRegistrations() {

        return registrationRepository.findAll();
    }

    // Get registrations for an event
    public List<Registration> getRegistrationsByEvent(
            Long eventId) {

        return registrationRepository.findByEventId(eventId);
    }

    // Get registrations by student email
    public List<Registration> getRegistrationsByEmail(
            String email) {

        return registrationRepository
                .findByEmailIgnoreCase(email);
    }

    // Count registrations for an event
    public long getRegistrationCount(Long eventId) {

        return registrationRepository
                .countByEventId(eventId);
    }

    // Calculate total tickets registered
    public long getTotalTickets(Long eventId) {

        return registrationRepository
                .findByEventId(eventId)
                .stream()
                .mapToLong(Registration::getTickets)
                .sum();
    }
}