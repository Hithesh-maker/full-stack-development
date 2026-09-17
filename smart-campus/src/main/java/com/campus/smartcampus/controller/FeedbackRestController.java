package com.campus.smartcampus.controller;

import com.campus.smartcampus.entity.Feedback;
import com.campus.smartcampus.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackRestController {

    private final FeedbackService feedbackService;

    public FeedbackRestController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    // GET all feedback for an event
    @GetMapping("/event/{eventId}")
    public List<Feedback> getEventFeedback(@PathVariable Long eventId) {
        return feedbackService.getFeedbackForEvent(eventId);
    }

    // GET feedback aggregate statistics for an event
    @GetMapping("/event/{eventId}/stats")
    public ResponseEntity<Map<String, Object>> getEventFeedbackStats(@PathVariable Long eventId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("eventId", eventId);
        stats.put("averageRating", feedbackService.getAverageRating(eventId));
        stats.put("totalReviews", feedbackService.getReviewCount(eventId));
        return ResponseEntity.ok(stats);
    }

    // POST submit feedback
    @PostMapping
    public ResponseEntity<Feedback> submitFeedback(@Valid @RequestBody Feedback feedback) {
        Feedback saved = feedbackService.submitFeedback(feedback);
        return ResponseEntity.ok(saved);
    }

    // DELETE feedback
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Feedback deleted successfully");
        return ResponseEntity.ok(response);
    }
}
