package com.campus.smartcampus.controller;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.service.EventService;
import com.campus.smartcampus.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.campus.smartcampus.config.CampusSecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

@Controller
public class EventController {

    private final EventService eventService;
    private final RegistrationService registrationService;
    private final CampusSecurityService campusSecurityService;
    private final com.campus.smartcampus.service.EventSessionService eventSessionService;

    public EventController(
            EventService eventService,
            RegistrationService registrationService,
            CampusSecurityService campusSecurityService,
            com.campus.smartcampus.service.EventSessionService eventSessionService) {

        this.eventService = eventService;
        this.registrationService = registrationService;
        this.campusSecurityService = campusSecurityService;
        this.eventSessionService = eventSessionService;
    }

    // =========================================================
    // HOME PAGE
    // =========================================================

    @GetMapping("/")
    public String home(Model model) {
        List<Event> upcomingEvents = eventService.getUpcomingEvents();
        model.addAttribute("events", upcomingEvents);
        model.addAttribute("totalEvents", eventService.getTotalEventsCount());
        model.addAttribute("upcomingCount", upcomingEvents.size());
        model.addAttribute("totalRegistrations", eventService.getTotalRegistrationsCount());

        return "index";
    }

    // =========================================================
    // ALL EVENTS (WITH SEARCH & FILTER)
    // =========================================================

    @GetMapping("/events")
    public String events(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        List<Event> events;
        if ((keyword != null && !keyword.isBlank()) ||
            (department != null && !department.isBlank()) ||
            (type != null && !type.isBlank()) ||
            date != null) {
            events = eventService.searchEvents(keyword, department, type, date);
        } else {
            events = eventService.getAllEvents();
        }

        model.addAttribute("events", events);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("selectedDepartment", department != null ? department : "");
        model.addAttribute("selectedType", type != null ? type : "");
        model.addAttribute("selectedDate", date != null ? date.toString() : "");

        return "events";
    }

    // =========================================================
    // EVENT DETAILS
    // =========================================================

    @GetMapping("/events/{id}")
    public String eventDetails(
            @PathVariable Long id,
            Model model) {

        Event event = eventService.getEventById(id);
        model.addAttribute("event", event);
        model.addAttribute("availableSeats", event.getAvailableSeats());
        model.addAttribute("relatedEvents", eventService.getRelatedEvents(id));
        model.addAttribute("googleCalendarUrl", buildGoogleCalendarUrl(event));
        model.addAttribute("sessions", eventSessionService.getSessionsByEventId(id));

        return "event-details";
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
                    "&text=" + java.net.URLEncoder.encode(event.getTitle(), java.nio.charset.StandardCharsets.UTF_8) +
                    "&dates=" + dates +
                    "&details=" + java.net.URLEncoder.encode(event.getDescription() != null ? event.getDescription() : "", java.nio.charset.StandardCharsets.UTF_8) +
                    "&location=" + java.net.URLEncoder.encode(event.getVenue() + " - " + event.getBuildingBlock(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "#";
        }
    }

    // =========================================================
    // ADMIN - EVENT LIST & DASHBOARD
    // =========================================================

    @GetMapping("/admin/events")
    public String adminEvents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {

        List<Event> events;
        if ((keyword != null && !keyword.isBlank()) ||
            (department != null && !department.isBlank()) ||
            (type != null && !type.isBlank()) ||
            date != null) {
            events = eventService.searchEvents(keyword, department, type, date);
        } else {
            events = eventService.getAllEvents();
        }

        model.addAttribute("events", events);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("selectedDepartment", department != null ? department : "");
        model.addAttribute("selectedType", type != null ? type : "");
        model.addAttribute("selectedDate", date != null ? date.toString() : "");

        // Dashboard summary stats
        model.addAttribute("totalEvents", eventService.getTotalEventsCount());
        model.addAttribute("upcomingEvents", eventService.getUpcomingEventsCount());
        model.addAttribute("totalRegistrations", eventService.getTotalRegistrationsCount());
        model.addAttribute("totalTickets", eventService.getTotalTicketsSold());

        // RBAC Context for Template
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = campusSecurityService.isAdmin(auth);
        boolean isCoordinator = campusSecurityService.isCoordinator(auth);
        String userDept = campusSecurityService.getUserDepartment(auth);

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isCoordinator", isCoordinator);
        model.addAttribute("userDept", userDept);
        model.addAttribute("coordinators", campusSecurityService.getAllCoordinators());
        model.addAttribute("campusSecurityService", campusSecurityService);
        model.addAttribute("currentAuth", auth);

        return "admin-events";
    }

    // =========================================================
    // ADMIN - VIEW STUDENTS ENROLLED IN AN EVENT
    // =========================================================

    @GetMapping("/admin/events/{id}/registrations")
    public String eventRegistrations(
            @PathVariable Long id,
            Model model) {

        Event event = eventService.getEventById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!campusSecurityService.canManageEvent(auth, event)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Access Denied: You cannot view registrations for an event from another department (" + event.getDepartment() + ").");
        }

        model.addAttribute("event", event);
        model.addAttribute("registrations", registrationService.getRegistrationsByEvent(id));
        model.addAttribute("totalTickets", registrationService.getTotalTickets(id));
        model.addAttribute("registrationCount", registrationService.getRegistrationCount(id));

        return "admin-event-registrations";
    }

    // =========================================================
    // ADMIN - ADD EVENT FORM
    // =========================================================

    @GetMapping("/admin/events/add")
    public String addEventForm(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Event event = new Event();

        if (campusSecurityService.isCoordinator(auth)) {
            String dept = campusSecurityService.getUserDepartment(auth);
            event.setDepartment(dept);
            model.addAttribute("isCoordinator", true);
            model.addAttribute("lockedDepartment", dept);
        } else {
            model.addAttribute("isCoordinator", false);
            model.addAttribute("lockedDepartment", null);
        }

        model.addAttribute("event", event);
        return "event-form";
    }

    // =========================================================
    // ADMIN - SAVE EVENT
    // =========================================================

    @PostMapping("/admin/events/save")
    public String saveEvent(
            @Valid @ModelAttribute("event") Event event,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Enforce department lockdown for coordinators
        if (campusSecurityService.isCoordinator(auth)) {
            String dept = campusSecurityService.getUserDepartment(auth);
            if (dept != null) {
                event.setDepartment(dept);
            }
        }

        if (result.hasErrors()) {
            return "event-form";
        }

        eventService.createEvent(event);
        redirectAttributes.addFlashAttribute("successMessage", "Event created successfully!");

        return "redirect:/admin/events";
    }

    // =========================================================
    // ADMIN - EDIT EVENT
    // =========================================================

    @GetMapping("/admin/events/edit/{id}")
    public String editEvent(
            @PathVariable Long id,
            Model model) {

        Event event = eventService.getEventById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!campusSecurityService.canManageEvent(auth, event)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Access Denied: You cannot modify an event belonging to another department (" + event.getDepartment() + ").");
        }

        boolean isCoord = campusSecurityService.isCoordinator(auth);
        model.addAttribute("isCoordinator", isCoord);
        model.addAttribute("lockedDepartment", isCoord ? campusSecurityService.getUserDepartment(auth) : null);
        model.addAttribute("event", event);
        return "event-form";
    }

    // =========================================================
    // ADMIN - UPDATE EVENT
    // =========================================================

    @PostMapping("/admin/events/update/{id}")
    public String updateEvent(
            @PathVariable Long id,
            @Valid @ModelAttribute("event") Event event,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Event existing = eventService.getEventById(id);

        if (!campusSecurityService.canManageEvent(auth, existing)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Access Denied: You cannot modify an event belonging to another department (" + existing.getDepartment() + ").");
        }

        if (campusSecurityService.isCoordinator(auth)) {
            // Coordinator cannot reassign event department
            event.setDepartment(existing.getDepartment());
        }

        if (result.hasErrors()) {
            return "event-form";
        }

        eventService.updateEvent(id, event);
        redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully!");

        return "redirect:/admin/events";
    }

    // =========================================================
    // ADMIN - DELETE EVENT (SECURE POST)
    // =========================================================

    @PostMapping("/admin/events/delete/{id}")
    public String deleteEvent(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (!campusSecurityService.canDeleteEvent(auth)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Access Denied: Coordinators are not authorized to delete campus events. Only Super Administrators can delete events.");
        }

        eventService.deleteEvent(id);
        redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully!");

        return "redirect:/admin/events";
    }

    // Deprecated GET delete route: Rejects mutating GET requests for RFC 7231 & CSRF safety
    @GetMapping("/admin/events/delete/{id}")
    public String deleteEventGet(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "Security Notice: Events cannot be deleted via a direct link. Please use the administrative delete button.");
        return "redirect:/admin/events";
    }
}