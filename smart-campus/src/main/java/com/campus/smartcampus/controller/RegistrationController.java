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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.campus.smartcampus.config.CampusSecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import com.campus.smartcampus.dto.TeamRegistrationForm;
import com.campus.smartcampus.entity.Team;
import com.campus.smartcampus.entity.TeamMember;
import com.campus.smartcampus.service.TeamService;

@Controller
public class RegistrationController {

    private final RegistrationService registrationService;
    private final EventService eventService;
    private final com.campus.smartcampus.service.QrCodeService qrCodeService;
    private final CampusSecurityService campusSecurityService;
    private final TeamService teamService;

    public RegistrationController(
            RegistrationService registrationService,
            EventService eventService,
            com.campus.smartcampus.service.QrCodeService qrCodeService,
            CampusSecurityService campusSecurityService,
            TeamService teamService) {

        this.registrationService = registrationService;
        this.eventService = eventService;
        this.qrCodeService = qrCodeService;
        this.campusSecurityService = campusSecurityService;
        this.teamService = teamService;
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

        Registration registration = new Registration();
        registration.setEventId(eventId);
        registration.setTickets(1);

        model.addAttribute("event", event);
        model.addAttribute("availableSeats", event.getAvailableSeats());
        model.addAttribute("registration", registration);
        model.addAttribute("isWaitlist", event.isSoldOut());

        return "registration";
    }

    // Process registration
    @PostMapping("/events/register")
    public String register(
            @Valid @ModelAttribute("registration") Registration registration,
            BindingResult result,
            @RequestParam(required = false) String paymentMethod,
            Model model) {

        Event event = eventService.getEventById(registration.getEventId());
        model.addAttribute("event", event);
        model.addAttribute("availableSeats", event.getAvailableSeats());
        model.addAttribute("isWaitlist", false);

        if (result.hasErrors()) {
            return "registration";
        }

        // If event is paid, record mock payment details
        if (event.isPaid()) {
            registration.setPaymentAmount(event.getPrice() * registration.getTickets());
            registration.setPaymentId("CAMPUS-TXN-" + System.currentTimeMillis() % 1000000);
        }

        try {
            Registration saved = registrationService.registerStudent(registration);

            String qrBase64 = qrCodeService.generateQrCodeBase64(saved.getToken(), 200, 200);

            model.addAttribute("message", "Registration successful! Here is your official campus pass.");
            model.addAttribute("registered", saved);
            model.addAttribute("event", event);
            model.addAttribute("qrCodeBase64", qrBase64);
            model.addAttribute("googleCalendarUrl", buildGoogleCalendarUrl(event));

            return "registration-success";

        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "registration";
        }
    }

    // Process waitlist entry
    @PostMapping("/events/waitlist")
    public String joinWaitlist(
            @Valid @ModelAttribute("registration") Registration registration,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        Event event = eventService.getEventById(registration.getEventId());
        model.addAttribute("event", event);

        if (result.hasFieldErrors("studentName") || result.hasFieldErrors("email") || result.hasFieldErrors("department")) {
            model.addAttribute("isWaitlist", true);
            return "registration";
        }

        try {
            Registration saved = registrationService.joinWaitlist(registration);
            redirectAttributes.addFlashAttribute("successMessage",
                    "You are now on the waitlist (Queue Position #" + saved.getWaitlistPosition() +
                    ")! If any attendee cancels, you will be automatically promoted.");
            return "redirect:/my-registrations?email=" + saved.getEmail();
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("isWaitlist", true);
            return "registration";
        }
    }

    private String buildGoogleCalendarUrl(Event event) {
        if (event == null || event.getDate() == null) return "#";
        try {
            var startDt = java.time.LocalDateTime.of(event.getDate(),
                    event.getTime() != null ? event.getTime() : java.time.LocalTime.of(9, 0));
            var endDt = startDt.plusHours(2);
            var dtf = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss");
            String dates = startDt.format(dtf) + "/" + endDt.format(dtf);

            return "https://calendar.google.com/calendar/render?action=TEMPLATE" +
                    "&text=" + java.net.URLEncoder.encode(event.getTitle(), StandardCharsets.UTF_8) +
                    "&dates=" + dates +
                    "&details=" + java.net.URLEncoder.encode(event.getDescription() != null ? event.getDescription() : "", StandardCharsets.UTF_8) +
                    "&location=" + java.net.URLEncoder.encode(event.getVenue() + " - " + event.getBuildingBlock(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "#";
        }
    }

    // View registrations for a student
    @GetMapping("/my-registrations")
    public String myRegistrations(
            @RequestParam(required = false) String email,
            jakarta.servlet.http.HttpServletRequest request,
            Model model) {
        if (request != null) {
            request.getSession(true);
        }

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

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Event event = eventService.getEventById(eventId);
        if (!campusSecurityService.canManageEvent(auth, event)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Access Denied: You cannot modify registrations for an event from another department (" + event.getDepartment() + ").");
        }

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Event event = eventService.getEventById(id);
        if (!campusSecurityService.canManageEvent(auth, event)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Access Denied: You cannot export registrations for an event from another department (" + event.getDepartment() + ").");
        }

        String csvData = registrationService.generateCsvForEvent(id);

        byte[] output = csvData.getBytes(StandardCharsets.UTF_8);

        String filename = "attendees-event-" + id + ".csv";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(output);
    }

    // Bulk upload registrations via CSV
    @PostMapping("/admin/events/{eventId}/registrations/bulk-upload")
    public String bulkUploadCsv(
            @PathVariable Long eventId,
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Event event = eventService.getEventById(eventId);
        if (!campusSecurityService.canManageEvent(auth, event)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Access Denied: You cannot bulk enroll attendees for an event from another department (" + event.getDepartment() + ").");
        }

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select a valid non-empty CSV file.");
            return "redirect:/admin/events/" + eventId + "/registrations";
        }

        try {
            RegistrationService.BulkUploadResult result = registrationService.bulkRegisterFromCsv(eventId, file.getInputStream());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Bulk enrollment complete: " + result.getSuccessCount() + " students registered successfully ("
                            + result.getDuplicateCount() + " duplicates skipped).");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bulk upload failed: " + e.getMessage());
        }

        return "redirect:/admin/events/" + eventId + "/registrations";
    }

    // =========================================================
    // HACKATHON / WORKSHOP TEAM DELEGATION REGISTRATION
    // =========================================================

    @GetMapping("/events/{eventId}/register/team")
    public String teamRegistrationForm(
            @PathVariable Long eventId,
            Model model,
            RedirectAttributes redirectAttributes) {

        Event event = eventService.getEventById(eventId);

        if (event.isPast()) {
            redirectAttributes.addFlashAttribute("errorMessage", "This event has already taken place. Team registration is closed.");
            return "redirect:/events/" + eventId;
        }

        if (event.isSoldOut()) {
            redirectAttributes.addFlashAttribute("errorMessage", "This event has reached maximum capacity.");
            return "redirect:/events/" + eventId;
        }

        TeamRegistrationForm form = new TeamRegistrationForm();
        form.setEventId(eventId);
        // Pre-allocate slots for up to 3 additional members (Leader + 3 = 4 max)
        form.getMembers().add(new TeamRegistrationForm.MemberEntry("", "", ""));
        form.getMembers().add(new TeamRegistrationForm.MemberEntry("", "", ""));
        form.getMembers().add(new TeamRegistrationForm.MemberEntry("", "", ""));

        model.addAttribute("event", event);
        model.addAttribute("teamForm", form);
        return "team-registration";
    }

    @PostMapping("/events/{eventId}/register/team")
    public String processTeamRegistration(
            @PathVariable Long eventId,
            @Valid @ModelAttribute("teamForm") TeamRegistrationForm teamForm,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        Event event = eventService.getEventById(eventId);
        teamForm.setEventId(eventId);

        if (result.hasErrors()) {
            model.addAttribute("event", event);
            return "team-registration";
        }

        try {
            Team team = teamService.registerTeam(teamForm);
            return "redirect:/registration/team-success/" + team.getTeamCode();
        } catch (Exception ex) {
            model.addAttribute("event", event);
            model.addAttribute("errorMessage", ex.getMessage());
            return "team-registration";
        }
    }

    @GetMapping("/registration/team-success/{teamCode}")
    public String teamRegistrationSuccess(
            @PathVariable String teamCode,
            Model model) {

        Team team = teamService.getTeamByCode(teamCode).orElse(null);
        if (team == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found with code: " + teamCode);
        }

        List<TeamMember> members = teamService.getTeamMembers(team.getId());
        model.addAttribute("team", team);
        model.addAttribute("event", team.getEvent());
        model.addAttribute("members", members);
        return "team-registration-success";
    }
}