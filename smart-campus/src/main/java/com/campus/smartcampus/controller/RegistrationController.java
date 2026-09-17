package com.campus.smartcampus.controller;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Registration;
import com.campus.smartcampus.service.EventService;
import com.campus.smartcampus.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
public class RegistrationController {

    private final RegistrationService registrationService;
    private final EventService eventService;

    public RegistrationController(
            RegistrationService registrationService,
            EventService eventService) {

        this.registrationService = registrationService;
        this.eventService = eventService;
    }

    // Registration form
    @GetMapping("/events/{eventId}/register")
    public String registrationForm(
            @PathVariable Long eventId,
            Model model,
            RedirectAttributes redirectAttributes) {

        Event event = eventService.getEventById(eventId);

        if (event.isPast()) {
            redirectAttributes.addFlashAttribute("errorMessage", "This event has already taken place. Registration is closed.");
            return "redirect:/events/" + eventId;
        }

        if (event.isSoldOut()) {
            redirectAttributes.addFlashAttribute("errorMessage", "This event is completely booked. No more seats are available.");
            return "redirect:/events/" + eventId;
        }

        Registration registration = new Registration();
        registration.setEventId(eventId);
        registration.setTickets(1);

        model.addAttribute("event", event);
        model.addAttribute("availableSeats", event.getAvailableSeats());
        model.addAttribute("registration", registration);

        return "registration";
    }

    // Process registration
    @PostMapping("/events/register")
    public String register(
            @Valid @ModelAttribute("registration") Registration registration,
            BindingResult result,
            Model model) {

        Event event = eventService.getEventById(registration.getEventId());
        model.addAttribute("event", event);
        model.addAttribute("availableSeats", event.getAvailableSeats());

        if (result.hasErrors()) {
            return "registration";
        }

        try {
            Registration saved = registrationService.registerStudent(registration);

            model.addAttribute("message", "Registration successful! Here is your event pass.");
            model.addAttribute("registered", saved);
            model.addAttribute("event", event);

            return "registration-success";

        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "registration";
        }
    }

    // View registrations for a student
    @GetMapping("/my-registrations")
    public String myRegistrations(
            @RequestParam(required = false) String email,
            Model model) {

        if (email != null && !email.isBlank()) {
            List<Registration> list = registrationService.getRegistrationsByEmail(email);
            model.addAttribute("registrations", list);
            model.addAttribute("email", email.trim());
            model.addAttribute("searched", true);
        } else {
            model.addAttribute("registrations", List.of());
            model.addAttribute("email", "");
            model.addAttribute("searched", false);
        }

        return "my-registrations";
    }

    // Student cancels own registration
    @PostMapping("/my-registrations/cancel/{id}")
    public String cancelStudentRegistration(
            @PathVariable Long id,
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {

        try {
            registrationService.cancelRegistration(id, email);
            redirectAttributes.addFlashAttribute("successMessage", "Registration cancelled successfully. Your seat has been released.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/my-registrations?email=" + email;
    }

    // Admin cancels / deletes an attendee registration
    @PostMapping("/admin/events/{eventId}/registrations/delete/{registrationId}")
    public String adminCancelRegistration(
            @PathVariable Long eventId,
            @PathVariable Long registrationId,
            RedirectAttributes redirectAttributes) {

        try {
            registrationService.adminCancelRegistration(registrationId);
            redirectAttributes.addFlashAttribute("successMessage", "Attendee registration cancelled.");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/events/" + eventId + "/registrations";
    }

    // Export attendee list to CSV
    @GetMapping("/admin/events/{id}/export-csv")
    public ResponseEntity<byte[]> exportAttendeesCsv(@PathVariable Long id) {
        Event event = eventService.getEventById(id);
        String csvData = registrationService.generateCsvForEvent(id);

        byte[] output = csvData.getBytes(StandardCharsets.UTF_8);

        String filename = "attendees-event-" + id + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(output);
    }
}