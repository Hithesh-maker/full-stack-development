package com.campus.smartcampus.controller;

import com.campus.smartcampus.config.CampusSecurityService;
import com.campus.smartcampus.entity.Registration;
import com.campus.smartcampus.service.EventService;
import com.campus.smartcampus.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
public class AdminScannerController {

    private final RegistrationService registrationService;
    private final EventService eventService;
    private final CampusSecurityService campusSecurityService;

    public AdminScannerController(
            RegistrationService registrationService,
            EventService eventService,
            CampusSecurityService campusSecurityService) {
        this.registrationService = registrationService;
        this.eventService = eventService;
        this.campusSecurityService = campusSecurityService;
    }

    // Live door scanner page with scan history
    @GetMapping("/admin/scanner")
    public String scannerPage(
            @RequestParam(required = false) Long eventId,
            Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        model.addAttribute("selectedEventId", eventId);

        var history = registrationService.getCheckedInHistory(eventId);
        model.addAttribute("checkedInHistory", history);
        model.addAttribute("totalCheckedInCount", history.size());
        return "admin-scanner";
    }

    // REST endpoint for camera barcode scanner (JSON payload)
    @PostMapping(value = "/api/admin/checkin", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkInJson(@RequestBody(required = false) Map<String, String> payload) {
        String rawToken = null;
        if (payload != null) {
            rawToken = payload.getOrDefault("token", payload.get("qrCode"));
        }
        return processCheckInToken(rawToken);
    }

    // REST endpoint for scanner verify (form submission / external barcode reader)
    @PostMapping(value = {"/api/admin/checkin", "/admin/scanner/verify"}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkInForm(
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String qrCode) {
        String rawToken = (token != null && !token.isBlank()) ? token : qrCode;
        return processCheckInToken(rawToken);
    }

    // Fallback REST endpoint for scanner verification (query params or generic)
    @PostMapping(value = {"/api/admin/checkin", "/admin/scanner/verify"})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkInFallback(
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String qrCode) {
        String rawToken = (token != null && !token.isBlank()) ? token : qrCode;
        return processCheckInToken(rawToken);
    }

    private ResponseEntity<Map<String, Object>> processCheckInToken(String rawToken) {

        Map<String, Object> response = new HashMap<>();

        if (rawToken == null || rawToken.trim().isBlank()) {
            response.put("success", false);
            response.put("message", "Ticket admission code / token cannot be empty.");
            return ResponseEntity.badRequest().body(response);
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (campusSecurityService.isCoordinator(auth)) {
            String coordDept = campusSecurityService.getUserDepartment(auth);
            try {
                Registration preview = registrationService.getRegistrationByToken(rawToken.trim());
                if (preview != null && preview.getEvent() != null) {
                    String eventDept = preview.getEvent().getDepartment();
                    if (coordDept != null && eventDept != null && !coordDept.trim().equalsIgnoreCase(eventDept.trim())) {
                        response.put("success", false);
                        response.put("message", "Access Denied: You are coordinator for " + coordDept +
                                " and cannot scan tickets for " + eventDept + " events.");
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                    }
                }
            } catch (Exception ignored) {}
        }

        try {
            Registration reg = registrationService.checkInStudent(rawToken.trim());
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm a, MMM dd");

            response.put("success", true);
            response.put("message", "Check-in successful! Welcome, " + reg.getStudentName() + ".");
            response.put("id", reg.getId());
            response.put("eventId", reg.getEventId());
            response.put("studentName", reg.getStudentName());
            response.put("email", reg.getEmail());
            response.put("department", reg.getDepartment());
            response.put("token", reg.getToken());
            response.put("tickets", reg.getTickets());
            response.put("eventTitle", reg.getEvent() != null ? reg.getEvent().getTitle() : "Event");
            response.put("checkedInAt", reg.getCheckedInAt() != null ? reg.getCheckedInAt().format(dtf) : "Now");

            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Manual toggle check-in from event registrations table or door scanner
    @PostMapping("/admin/registrations/{id}/toggle-checkin")
    public String toggleCheckIn(
            @PathVariable Long id,
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) String returnTo,
            RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        try {
            Registration target = registrationService.getRegistrationById(id);
            if (target != null && target.getEvent() != null && !campusSecurityService.canManageEvent(auth, target.getEvent())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Access Denied: You cannot modify attendance for another department's event.");
                return "scanner".equalsIgnoreCase(returnTo) ? "redirect:/admin/scanner" : "redirect:/admin/events";
            }

            Registration reg = registrationService.toggleCheckIn(id);
            String statusMsg = "CHECKED_IN".equalsIgnoreCase(reg.getStatus()) ?
                    "marked as Checked-In." : "reset to Confirmed.";
            redirectAttributes.addFlashAttribute("successMessage",
                    "Attendee " + reg.getStudentName() + " " + statusMsg);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        if ("scanner".equalsIgnoreCase(returnTo)) {
            return eventId != null ? "redirect:/admin/scanner?eventId=" + eventId : "redirect:/admin/scanner";
        }

        if (eventId != null) {
            return "redirect:/admin/events/" + eventId + "/registrations";
        }
        return "redirect:/admin/events";
    }
}
