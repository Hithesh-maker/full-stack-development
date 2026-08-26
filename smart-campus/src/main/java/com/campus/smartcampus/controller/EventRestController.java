package com.campus.smartcampus.controller;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.service.EventService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventRestController {

    private final EventService eventService;

    public EventRestController(EventService eventService) {
        this.eventService = eventService;
    }

    // GET all events
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    // GET event by ID
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                eventService.getEventById(id)
        );
    }

    // Search events
    @GetMapping("/search")
    public List<Event> searchEvents(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String type) {

        return eventService.searchEvents(
                department,
                type
        );
    }

    // CREATE event
    @PostMapping
    public ResponseEntity<Event> createEvent(
            @Valid @RequestBody Event event) {

        return ResponseEntity.ok(
                eventService.createEvent(event)
        );
    }

    // UPDATE event
    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody Event event) {

        return ResponseEntity.ok(
                eventService.updateEvent(id, event)
        );
    }

    // DELETE event
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(
            @PathVariable Long id) {

        eventService.deleteEvent(id);

        return ResponseEntity.ok(
                "Event deleted successfully"
        );
    }
}