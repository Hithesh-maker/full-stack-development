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

import java.time.LocalDate;
import java.util.List;

@Controller
public class EventController {

    private final EventService eventService;
    private final RegistrationService registrationService;

    public EventController(
            EventService eventService,
            RegistrationService registrationService) {

        this.eventService = eventService;
        this.registrationService = registrationService;
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

        return "event-details";
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
        model.addAttribute("event", new Event());
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

        model.addAttribute("event", eventService.getEventById(id));
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

        eventService.deleteEvent(id);
        redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully!");

        return "redirect:/admin/events";
    }

    // Backward compatible GET delete fallback
    @GetMapping("/admin/events/delete/{id}")
    public String deleteEventGet(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        eventService.deleteEvent(id);
        redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully!");

        return "redirect:/admin/events";
    }
}