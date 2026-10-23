package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Registration;
import com.campus.smartcampus.repository.EventRepository;
import com.campus.smartcampus.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final NotificationService notificationService;

    @org.springframework.beans.factory.annotation.Autowired
    public RegistrationService(
            RegistrationRepository registrationRepository,
            EventRepository eventRepository,
            @org.springframework.beans.factory.annotation.Autowired(required = false) NotificationService notificationService) {

        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.notificationService = notificationService;
    }

    public RegistrationService(
            RegistrationRepository registrationRepository,
            EventRepository eventRepository) {
        this(registrationRepository, eventRepository, null);
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
        boolean alreadyRegistered = registrationRepository.existsActiveRegistration(
                event.getId(),
                registration.getEmail().trim()
        ) || registrationRepository.existsByEventIdAndEmailIgnoreCaseAndStatus(
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

        if (registration.getToken() == null || registration.getToken().isBlank()) {
            registration.setToken(registration.generateToken());
        }

        if (event.isPaid()) {
            if (registration.getPaymentAmount() == null || registration.getPaymentAmount() <= 0) {
                registration.setPaymentAmount(event.getPrice() * registration.getTickets());
            }
            if (registration.getPaymentId() == null || registration.getPaymentId().isBlank()) {
                registration.setPaymentId("PAY-TXN-" + (System.currentTimeMillis() % 1000000));
            }
        }

        registration.setEvent(event);
        registration.setStatus("CONFIRMED");
        registration.setRegisteredAt(LocalDateTime.now());

        Registration saved = registrationRepository.save(registration);
        if (notificationService != null) {
            try { notificationService.sendBookingConfirmation(saved); } catch (Exception ignored) {}
        }
        return saved;
    }

    // Join waitlist when event is sold out
    public Registration joinWaitlist(Registration registration) {
        if (registration.getEventId() == null) {
            throw new RuntimeException("Event ID is required");
        }

        Event event = eventRepository.findById(registration.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + registration.getEventId()));

        if (event.isPast()) {
            throw new RuntimeException("Cannot join waitlist for an event that has already ended.");
        }

        boolean alreadyActive = registrationRepository.existsActiveRegistration(
                event.getId(),
                registration.getEmail().trim()
        );

        if (alreadyActive) {
            throw new RuntimeException(
                    "You already have an active registration or waitlist entry with email: " + registration.getEmail().trim()
            );
        }

        registration.setEvent(event);
        registration.setTickets(1);
        registration.setStatus("WAITLISTED");
        registration.setRegisteredAt(LocalDateTime.now());

        long count = registrationRepository.countByEventIdAndStatus(event.getId(), "WAITLISTED");
        registration.setWaitlistPosition((int) count + 1);

        if (registration.getToken() == null || registration.getToken().isBlank()) {
            registration.setToken(registration.generateToken());
        }

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

    public Registration getRegistrationByToken(String token) {
        if (token == null || token.isBlank()) {
            throw new RuntimeException("Token cannot be blank");
        }
        return registrationRepository.findByToken(token.trim().toUpperCase())
                .orElseGet(() -> registrationRepository.findByToken(token.trim())
                        .orElseThrow(() -> new RuntimeException("No admission pass found for code: " + token)));
    }

    // Student self-cancellation
    public void cancelRegistration(Long id, String email) {
        if (email == null || email.trim().isBlank()) {
            throw new SecurityException("Unauthorized: Valid student email address is required to release this pass.");
        }
        Registration reg = getRegistrationById(id);
        if (!reg.getEmail().trim().equalsIgnoreCase(email.trim())) {
            throw new SecurityException("Unauthorized: Provided email address does not match this registration record.");
        }
        reg.setStatus("CANCELLED");
        registrationRepository.save(reg);

        // Auto-promote earliest waitlisted student
        promoteNextWaitlisted(reg.getEventId());
    }

    // Admin cancellation
    public void adminCancelRegistration(Long id) {
        Registration reg = getRegistrationById(id);
        reg.setStatus("CANCELLED");
        registrationRepository.save(reg);

        // Auto-promote earliest waitlisted student
        promoteNextWaitlisted(reg.getEventId());
    }

    // Auto-promote FIFO waitlist candidate
    public Registration promoteNextWaitlisted(Long eventId) {
        if (eventId == null) return null;
        var opt = registrationRepository.findFirstByEventIdAndStatusOrderByIdAsc(eventId, "WAITLISTED");
        if (opt.isPresent()) {
            Registration next = opt.get();
            next.setStatus("CONFIRMED");
            next.setWaitlistPosition(null);
            if (next.getToken() == null || next.getToken().isBlank()) {
                next.setToken(next.generateToken());
            }
            Registration promoted = registrationRepository.save(next);

            // Re-sequence remaining waitlist positions
            List<Registration> remainingWaitlist = registrationRepository.findByEventIdAndStatusOrderByIdAsc(eventId, "WAITLISTED");
            if (remainingWaitlist != null) {
                int pos = 1;
                for (Registration rem : remainingWaitlist) {
                    rem.setWaitlistPosition(pos++);
                    registrationRepository.save(rem);
                }
            }

            if (notificationService != null) {
                try { notificationService.sendWaitlistPromotionAlert(promoted); } catch (Exception ignored) {}
            }
            return promoted;
        }
        return null;
    }

    // Fast-track Check-in at the door via QR Token
    public Registration checkInStudent(String token) {
        Registration reg = getRegistrationByToken(token);

        if ("CHECKED_IN".equalsIgnoreCase(reg.getStatus())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a, MMM dd");
            String timeStr = reg.getCheckedInAt() != null ? reg.getCheckedInAt().format(formatter) : "earlier";
            throw new RuntimeException("Duplicate Entry! Attendee " + reg.getStudentName() +
                    " was already checked in at " + timeStr);
        }

        if ("CANCELLED".equalsIgnoreCase(reg.getStatus())) {
            throw new RuntimeException("Entry Denied: This ticket registration was cancelled.");
        }

        if ("WAITLISTED".equalsIgnoreCase(reg.getStatus())) {
            throw new RuntimeException("Entry Denied: Attendee is currently on the waitlist (Position #" +
                    reg.getWaitlistPosition() + ") and does not have an active confirmed seat.");
        }

        reg.setStatus("CHECKED_IN");
        reg.setCheckedInAt(LocalDateTime.now());
        Registration checkedIn = registrationRepository.save(reg);
        if (notificationService != null) {
            try { notificationService.sendCheckInWelcome(checkedIn); } catch (Exception ignored) {}
        }
        return checkedIn;
    }

    // Retrieve checked-in scan history (all or filtered by event)
    public List<Registration> getCheckedInHistory(Long eventId) {
        if (eventId != null && eventId > 0) {
            return registrationRepository.findByEventIdAndStatusOrderByCheckedInAtDesc(eventId, "CHECKED_IN");
        }
        return registrationRepository.findByStatusOrderByCheckedInAtDesc("CHECKED_IN");
    }

    // Toggle check-in status from admin panel
    public Registration toggleCheckIn(Long registrationId) {
        Registration reg = getRegistrationById(registrationId);
        if ("CHECKED_IN".equalsIgnoreCase(reg.getStatus())) {
            reg.setStatus("CONFIRMED");
            reg.setCheckedInAt(null);
        } else if ("CONFIRMED".equalsIgnoreCase(reg.getStatus())) {
            reg.setStatus("CHECKED_IN");
            reg.setCheckedInAt(LocalDateTime.now());
        }
        return registrationRepository.save(reg);
    }

    // Delete registration completely
    public void deleteRegistration(Long id) {
        registrationRepository.deleteById(id);
    }

    public long getRegistrationCount(Long eventId) {
        return registrationRepository.countByEventIdAndStatus(eventId, "CONFIRMED")
                + registrationRepository.countByEventIdAndStatus(eventId, "CHECKED_IN");
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
        String sanitized = value;
        if (!sanitized.isEmpty()) {
            char firstChar = sanitized.charAt(0);
            if (firstChar == '=' || firstChar == '+' || firstChar == '-' || firstChar == '@' || firstChar == '\t' || firstChar == '\r') {
                sanitized = "'" + sanitized;
            }
        }
        if (sanitized.contains(",") || sanitized.contains("\"") || sanitized.contains("\n") || sanitized.contains("\r")) {
            return "\"" + sanitized.replace("\"", "\"\"") + "\"";
        }
        return sanitized;
    }

    // Bulk student CSV enrollment engine
    public BulkUploadResult bulkRegisterFromCsv(Long eventId, InputStream inputStream) {
        if (eventId == null) throw new RuntimeException("Event ID is required");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with ID: " + eventId));

        if (event.isPast()) {
            throw new RuntimeException("Cannot enroll students into a past event.");
        }

        BulkUploadResult result = new BulkUploadResult();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) continue;

                // Check for header row
                if (isFirstLine) {
                    isFirstLine = false;
                    String lower = line.toLowerCase();
                    if (lower.contains("name") || lower.contains("email") || lower.contains("department")) {
                        continue;
                    }
                }

                result.setTotalProcessed(result.getTotalProcessed() + 1);

                String[] parts = line.split(",", -1);
                if (parts.length < 2) {
                    result.setErrorCount(result.getErrorCount() + 1);
                    result.getErrorMessages().add("Line " + lineNumber + ": Insufficient columns (must provide at least name, email).");
                    continue;
                }

                String name = parts[0].trim().replace("\"", "");
                String email = parts[1].trim().replace("\"", "");
                String dept = parts.length > 2 ? parts[2].trim().replace("\"", "") : event.getDepartment();

                if (name.isBlank() || email.isBlank()) {
                    result.setErrorCount(result.getErrorCount() + 1);
                    result.getErrorMessages().add("Line " + lineNumber + ": Name or email cannot be empty.");
                    continue;
                }

                if (!email.contains("@") || !email.contains(".")) {
                    result.setErrorCount(result.getErrorCount() + 1);
                    result.getErrorMessages().add("Line " + lineNumber + " (" + email + "): Invalid email format.");
                    continue;
                }

                // Check duplicate
                if (registrationRepository.existsActiveRegistration(event.getId(), email)) {
                    result.setDuplicateCount(result.getDuplicateCount() + 1);
                    continue;
                }

                // Check capacity
                Long totalBooked = registrationRepository.sumTicketsByEventId(event.getId());
                long bookedCount = totalBooked != null ? totalBooked : 0L;
                if (bookedCount + 1 > event.getCapacity()) {
                    result.setErrorCount(result.getErrorCount() + 1);
                    result.getErrorMessages().add("Event reached max venue capacity (" + event.getCapacity() + " seats). Stopped enrollment at line " + lineNumber + ".");
                    break;
                }

                Registration reg = new Registration();
                reg.setEventId(event.getId());
                reg.setEvent(event);
                reg.setStudentName(name);
                reg.setEmail(email);
                reg.setDepartment(dept.isBlank() ? event.getDepartment() : dept);
                reg.setTickets(1);
                reg.setStatus("CONFIRMED");
                reg.setRegisteredAt(LocalDateTime.now());
                reg.setToken(reg.generateToken());

                Registration saved = registrationRepository.save(reg);
                result.setSuccessCount(result.getSuccessCount() + 1);

                if (notificationService != null) {
                    try { notificationService.sendBookingConfirmation(saved); } catch (Exception ignored) {}
                }
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read CSV stream: " + e.getMessage(), e);
        }

        return result;
    }

    public static class BulkUploadResult {
        private int totalProcessed;
        private int successCount;
        private int duplicateCount;
        private int errorCount;
        private final List<String> errorMessages = new ArrayList<>();

        public int getTotalProcessed() { return totalProcessed; }
        public void setTotalProcessed(int totalProcessed) { this.totalProcessed = totalProcessed; }
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getDuplicateCount() { return duplicateCount; }
        public void setDuplicateCount(int duplicateCount) { this.duplicateCount = duplicateCount; }
        public int getErrorCount() { return errorCount; }
        public void setErrorCount(int errorCount) { this.errorCount = errorCount; }
        public List<String> getErrorMessages() { return errorMessages; }
    }
}