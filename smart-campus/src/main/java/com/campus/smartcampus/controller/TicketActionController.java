package com.campus.smartcampus.controller;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Registration;
import com.campus.smartcampus.service.CertificateService;
import com.campus.smartcampus.service.EventService;
import com.campus.smartcampus.service.PdfTicketService;
import com.campus.smartcampus.service.RegistrationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class TicketActionController {

    private final RegistrationService registrationService;
    private final EventService eventService;
    private final PdfTicketService pdfTicketService;
    private final CertificateService certificateService;

    public TicketActionController(
            RegistrationService registrationService,
            EventService eventService,
            PdfTicketService pdfTicketService,
            CertificateService certificateService) {
        this.registrationService = registrationService;
        this.eventService = eventService;
        this.pdfTicketService = pdfTicketService;
        this.certificateService = certificateService;
    }

    // Download / view admission pass PDF by token (Secure route)
    @GetMapping("/registrations/pass/{token}/pdf")
    public ResponseEntity<byte[]> downloadTicketPdfByToken(@PathVariable String token) {
        Registration registration = registrationService.getRegistrationByToken(token);
        if (registration == null) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Admission pass not found for token: " + token);
        }
        return buildTicketPdfResponse(registration);
    }

    // Download / view official conference lanyard badge PDF by token (Secure route)
    @GetMapping("/registrations/pass/{token}/badge")
    public ResponseEntity<byte[]> downloadBadgePdfByToken(@PathVariable String token) {
        Registration registration = registrationService.getRegistrationByToken(token);
        if (registration == null) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Admission pass not found for token: " + token);
        }
        return buildBadgePdfResponse(registration);
    }

    // Download / view admission pass PDF by numeric ID
    @GetMapping("/registrations/{id}/pdf")
    public ResponseEntity<byte[]> downloadTicketPdf(
            @PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String email) {
        Registration registration;
        try {
            registration = registrationService.getRegistrationById(id);
        } catch (RuntimeException ex) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Registration not found with ID: " + id);
        }

        if (email != null && !email.isBlank()) {
            if (!registration.getEmail().trim().equalsIgnoreCase(email.trim())) {
                throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Access Denied: Email does not match registration record.");
            }
        }

        return buildTicketPdfResponse(registration);
    }

    private ResponseEntity<byte[]> buildTicketPdfResponse(Registration registration) {
        byte[] pdfBytes = pdfTicketService.generateTicketPdf(registration);
        String token = registration.getToken() != null ? registration.getToken() : String.valueOf(registration.getId());
        String filename = "admission-pass-" + token + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    // Download / view e-certificate of participation PDF
    @GetMapping("/registrations/{id}/certificate")
    public ResponseEntity<byte[]> downloadCertificatePdf(
            @PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String email) {
        Registration registration;
        try {
            registration = registrationService.getRegistrationById(id);
        } catch (RuntimeException ex) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Registration not found with ID: " + id);
        }

        if (email != null && !email.isBlank()) {
            if (!registration.getEmail().trim().equalsIgnoreCase(email.trim())) {
                throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Access Denied: Email does not match registration record.");
            }
        }

        if (!registration.canDownloadCertificate()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "E-Certificate is available only after attending the event or after the event concludes.");
        }

        com.campus.smartcampus.entity.Certificate cert = certificateService.getOrCreateCertificate(registration);
        byte[] pdfBytes = pdfTicketService.generateCertificatePdf(registration, cert);
        String filename = "certificate-participation-" + id + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    // Download / view official conference lanyard badge PDF
    @GetMapping("/registrations/{id}/badge")
    public ResponseEntity<byte[]> downloadBadgePdf(
            @PathVariable Long id,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String email) {
        Registration registration;
        try {
            registration = registrationService.getRegistrationById(id);
        } catch (RuntimeException ex) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Registration not found with ID: " + id);
        }

        if (email != null && !email.isBlank()) {
            if (!registration.getEmail().trim().equalsIgnoreCase(email.trim())) {
                throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN, "Access Denied: Email does not match registration record.");
            }
        }

        return buildBadgePdfResponse(registration);
    }

    private ResponseEntity<byte[]> buildBadgePdfResponse(Registration registration) {
        byte[] pdfBytes = pdfTicketService.generateConferenceBadge(registration);
        String token = registration.getToken() != null ? registration.getToken() : String.valueOf(registration.getId());
        String filename = "delegate-badge-" + token + ".pdf";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    // Download iCalendar .ics file
    @GetMapping("/events/{id}/calendar.ics")
    public ResponseEntity<byte[]> downloadCalendarIcs(@PathVariable Long id) {
        Event event;
        try {
            event = eventService.getEventById(id);
        } catch (RuntimeException ex) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Event not found with ID: " + id);
        }

        LocalDateTime startDt = LocalDateTime.of(
                event.getDate() != null ? event.getDate() : LocalDate.now(),
                event.getTime() != null ? event.getTime() : java.time.LocalTime.of(9, 0));
        LocalDateTime endDt = startDt.plusHours(2);

        DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
        DateTimeFormatter utcFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");

        String startStr = startDt.format(dtFormatter);
        String endStr = endDt.format(dtFormatter);
        String nowStr = LocalDateTime.now().format(utcFormatter);

        String title = escapeIcs(event.getTitle());
        String description = escapeIcs(event.getDescription() + " (Department: " + event.getDepartment() + ")");
        String location = escapeIcs(event.getVenue() + " - " + event.getBuildingBlock());

        String icsContent = "BEGIN:VCALENDAR\r\n" +
                "VERSION:2.0\r\n" +
                "PRODID:-//Educare University//Smart Campus Events//EN\r\n" +
                "CALSCALE:GREGORIAN\r\n" +
                "METHOD:PUBLISH\r\n" +
                "BEGIN:VEVENT\r\n" +
                "UID:smartcampus-event-" + event.getId() + "@educare.edu\r\n" +
                "DTSTAMP:" + nowStr + "\r\n" +
                "DTSTART:" + startStr + "\r\n" +
                "DTEND:" + endStr + "\r\n" +
                "SUMMARY:" + title + "\r\n" +
                "DESCRIPTION:" + description + "\r\n" +
                "LOCATION:" + location + "\r\n" +
                "STATUS:CONFIRMED\r\n" +
                "END:VEVENT\r\n" +
                "END:VCALENDAR\r\n";

        byte[] icsBytes = icsContent.getBytes(StandardCharsets.UTF_8);
        String filename = "event-" + id + ".ics";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/calendar; charset=utf-8"))
                .body(icsBytes);
    }

    private String escapeIcs(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace(";", "\\;")
                   .replace(",", "\\,")
                   .replace("\n", "\\n")
                   .replace("\r", "");
    }
}
