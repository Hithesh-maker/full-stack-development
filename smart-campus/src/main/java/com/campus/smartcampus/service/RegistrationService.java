package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Registration;
import com.campus.smartcampus.repository.EventRepository;
import com.campus.smartcampus.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public Registration registerStudent(Registration registration) {
        if (registration.getEventId() == null) {
            throw new RuntimeException("Event ID is required");
        }

        Event event = eventRepository.findById(registration.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + registration.getEventId()));

        if (event.isPast()) {
            throw new RuntimeException("Cannot register for an event that has already ended.");
        }

        if (registration.getTickets() == null || registration.getTickets() <= 0) {
            throw new RuntimeException("Number of tickets must be at least 1.");
        }

        if (registration.getTickets() > 10) {
            throw new RuntimeException("Maximum 10 tickets allowed per registration.");
        }

        // Check for duplicate active registration for the same student email on this event
        boolean alreadyRegistered = registrationRepository.existsByEventIdAndEmailIgnoreCaseAndStatus(
                event.getId(),
                registration.getEmail().trim(),
                "CONFIRMED"
        );

        if (alreadyRegistered) {
            throw new RuntimeException(
                    "You have already registered for this event with email: " + registration.getEmail().trim()
            );
        }

        // Calculate available capacity
        Long bookedTickets = registrationRepository.sumTicketsByEventId(event.getId());
        long currentBooked = bookedTickets != null ? bookedTickets : 0;
        long availableSeats = event.getCapacity() - currentBooked;

        if (availableSeats <= 0) {
            throw new RuntimeException("This event is completely booked! No seats available.");
        }

        if (registration.getTickets() > availableSeats) {
            throw new RuntimeException(
                    "Only " + availableSeats + " " + (availableSeats == 1 ? "seat is" : "seats are") + " available."
            );
        }

        registration.setEvent(event);
        registration.setStatus("CONFIRMED");
        registration.setRegisteredAt(LocalDateTime.now());

        return registrationRepository.save(registration);
    }

    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    public List<Registration> getRegistrationsByEvent(Long eventId) {
        return registrationRepository.findByEventId(eventId);
    }

    public List<Registration> getRegistrationsByEmail(String email) {
        if (email == null || email.isBlank()) {
            return List.of();
        }
        return registrationRepository.findByEmailIgnoreCaseOrderByRegisteredAtDesc(email.trim());
    }

    public Registration getRegistrationById(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registration not found with ID: " + id));
    }

    // Student self-cancellation
    public void cancelRegistration(Long id, String email) {
        Registration reg = getRegistrationById(id);
        if (email != null && !email.isBlank()) {
            if (!reg.getEmail().equalsIgnoreCase(email.trim())) {
                throw new RuntimeException("Unauthorized: Email does not match this registration.");
            }
        }
        reg.setStatus("CANCELLED");
        registrationRepository.save(reg);
    }

    // Admin cancellation
    public void adminCancelRegistration(Long id) {
        Registration reg = getRegistrationById(id);
        reg.setStatus("CANCELLED");
        registrationRepository.save(reg);
    }

    // Delete registration completely
    public void deleteRegistration(Long id) {
        registrationRepository.deleteById(id);
    }

    public long getRegistrationCount(Long eventId) {
        return registrationRepository.countByEventIdAndStatus(eventId, "CONFIRMED");
    }

    public long getTotalTickets(Long eventId) {
        Long sum = registrationRepository.sumTicketsByEventId(eventId);
        return sum != null ? sum : 0L;
    }

    // CSV export for event attendees
    public String generateCsvForEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        List<Registration> registrations = registrationRepository.findByEventId(eventId);

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Student Name,Email,Department,Tickets,Status,Registered At,Event Title,Event Date\n");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Registration r : registrations) {
            String registeredDate = r.getRegisteredAt() != null ? r.getRegisteredAt().format(formatter) : "N/A";
            csv.append(r.getId()).append(",")
                    .append(escapeCsv(r.getStudentName())).append(",")
                    .append(escapeCsv(r.getEmail())).append(",")
                    .append(escapeCsv(r.getDepartment())).append(",")
                    .append(r.getTickets()).append(",")
                    .append(r.getStatus() != null ? r.getStatus() : "CONFIRMED").append(",")
                    .append(escapeCsv(registeredDate)).append(",")
                    .append(escapeCsv(event.getTitle())).append(",")
                    .append(event.getDate()).append("\n");
        }

        return csv.toString();
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}