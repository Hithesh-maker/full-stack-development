package com.campus.smartcampus.controller;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.service.EventService;
import com.campus.smartcampus.service.RegistrationService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

        model.addAttribute(
                "events",
                eventService.getUpcomingEvents()
        );

        return "index";
    }

    // =========================================================
    // ALL EVENTS
    // =========================================================

    @GetMapping("/events")
    public String events(Model model) {

        model.addAttribute(
                "events",
                eventService.getAllEvents()
        );

        return "events";
    }

    // =========================================================
    // EVENT DETAILS
    // =========================================================

    @GetMapping("/events/{id}")
    public String eventDetails(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "event",
                eventService.getEventById(id)
        );

        return "event-details";
    }

    // =========================================================
    // ADMIN - EVENT LIST
    // =========================================================

    @GetMapping("/admin/events")
    public String adminEvents(Model model) {

        model.addAttribute(
                "events",
                eventService.getAllEvents()
        );

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

        model.addAttribute(
                "event",
                event
        );

        model.addAttribute(
                "registrations",
                registrationService.getRegistrationsByEvent(id)
        );

        model.addAttribute(
                "totalTickets",
                registrationService.getTotalTickets(id)
        );

        model.addAttribute(
                "registrationCount",
                registrationService.getRegistrationCount(id)
        );

        return "admin-event-registrations";
    }

    // =========================================================
    // ADMIN - ADD EVENT FORM
    // =========================================================

    @GetMapping("/admin/events/add")
    public String addEventForm(Model model) {

        model.addAttribute(
                "event",
                new Event()
        );

        return "event-form";
    }

    // =========================================================
    // ADMIN - SAVE EVENT
    // =========================================================

    @PostMapping("/admin/events/save")
    public String saveEvent(
            @Valid @ModelAttribute("event") Event event,
            BindingResult result) {

        if (result.hasErrors()) {
            return "event-form";
        }

        eventService.createEvent(event);

        return "redirect:/admin/events";
    }

    // =========================================================
    // ADMIN - EDIT EVENT
    // =========================================================

    @GetMapping("/admin/events/edit/{id}")
    public String editEvent(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "event",
                eventService.getEventById(id)
        );

        return "event-form";
    }

    // =========================================================
    // ADMIN - UPDATE EVENT
    // =========================================================

    @PostMapping("/admin/events/update/{id}")
    public String updateEvent(
            @PathVariable Long id,
            @Valid @ModelAttribute("event") Event event,
            BindingResult result) {

        if (result.hasErrors()) {
            return "event-form";
        }

        eventService.updateEvent(id, event);

        return "redirect:/admin/events";
    }

    // =========================================================
    // ADMIN - DELETE EVENT
    // =========================================================

    @GetMapping("/admin/events/delete/{id}")
    public String deleteEvent(
            @PathVariable Long id) {

        eventService.deleteEvent(id);

        return "redirect:/admin/events";
    }
}