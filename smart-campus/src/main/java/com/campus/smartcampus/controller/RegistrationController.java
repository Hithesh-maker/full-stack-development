package com.campus.smartcampus.controller;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Registration;
import com.campus.smartcampus.service.EventService;
import com.campus.smartcampus.service.RegistrationService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
            Model model) {

        Event event = eventService.getEventById(eventId);

        Registration registration =
                new Registration();

        registration.setEventId(eventId);

        model.addAttribute(
                "event",
                event
        );

        model.addAttribute(
                "registration",
                registration
        );

        return "registration";
    }

    // Process registration
    @PostMapping("/events/register")
    public String register(
            @Valid @ModelAttribute("registration")
            Registration registration,
            BindingResult result,
            Model model) {

        Event event =
                eventService.getEventById(
                        registration.getEventId()
                );

        model.addAttribute("event", event);

        if (result.hasErrors()) {
            return "registration";
        }

        try {

            registrationService
                    .registerStudent(registration);

            model.addAttribute(
                    "message",
                    "Registration successful!"
            );

            model.addAttribute(
                    "registered",
                    registration
            );

            return "registration-success";

        } catch (RuntimeException ex) {

            model.addAttribute(
                    "error",
                    ex.getMessage()
            );

            return "registration";
        }
    }

    // View registrations for a student
    @GetMapping("/my-registrations")
    public String myRegistrations(
            @RequestParam String email,
            Model model) {

        model.addAttribute(
                "registrations",
                registrationService
                        .getRegistrationsByEmail(email)
        );

        model.addAttribute(
                "email",
                email
        );

        return "my-registrations";
    }
}