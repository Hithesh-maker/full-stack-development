package com.campus.smartcampus.controller;

import com.campus.smartcampus.entity.Registration;
import com.campus.smartcampus.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationRestController {

    private final RegistrationService registrationService;

    public RegistrationRestController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    // GET all registrations
    @GetMapping
    public List<Registration> getAllRegistrations() {
        return registrationService.getAllRegistrations();
    }

    // GET registration by ID
    @GetMapping("/{id}")
    public ResponseEntity<Registration> getRegistrationById(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.getRegistrationById(id));
    }

    // GET registrations by event
    @GetMapping("/event/{eventId}")
    public List<Registration> getRegistrationsByEvent(@PathVariable Long eventId) {
        return registrationService.getRegistrationsByEvent(eventId);
    }

    // GET registrations by student email
    @GetMapping("/student")
    public List<Registration> getRegistrationsByEmail(@RequestParam String email) {
        return registrationService.getRegistrationsByEmail(email);
    }

    // POST register student
    @PostMapping
    public ResponseEntity<Registration> register(@Valid @RequestBody Registration registration) {
        Registration saved = registrationService.registerStudent(registration);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // DELETE / cancel registration
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> cancelRegistration(
            @PathVariable Long id,
            @RequestParam(required = false) String email) {

        registrationService.cancelRegistration(id, email);
        return ResponseEntity.ok(Map.of("message", "Registration cancelled successfully"));
    }
}
