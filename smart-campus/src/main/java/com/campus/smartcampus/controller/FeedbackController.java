package com.campus.smartcampus.controller;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Feedback;
import com.campus.smartcampus.service.EventService;
import com.campus.smartcampus.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final EventService eventService;

    public FeedbackController(FeedbackService feedbackService, EventService eventService) {
        this.feedbackService = feedbackService;
        this.eventService = eventService;
    }

    // =========================================================
    // STUDENT FEEDBACK PAGE & REVIEWS
    // =========================================================

    @GetMapping("/events/{eventId}/feedback")
    public String showFeedbackPage(
            @PathVariable Long eventId,
            Model model) {

        Event event = eventService.getEventById(eventId);
        List<Feedback> reviews = feedbackService.getFeedbackForEvent(eventId);
        double avgRating = feedbackService.getAverageRating(eventId);
        long reviewCount = feedbackService.getReviewCount(eventId);

        Feedback feedback = new Feedback();
        feedback.setEventId(eventId);
        feedback.setRating(5); // default 5 stars

        model.addAttribute("event", event);
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgRating", avgRating);
        model.addAttribute("reviewCount", reviewCount);
        model.addAttribute("feedback", feedback);

        return "event-feedback";
    }

    // =========================================================
    // SUBMIT STUDENT FEEDBACK
    // =========================================================

    @PostMapping("/events/{eventId}/feedback")
    public String submitFeedback(
            @PathVariable Long eventId,
            @Valid @ModelAttribute("feedback") Feedback feedback,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        Event event = eventService.getEventById(eventId);
        feedback.setEventId(eventId);

        if (result.hasErrors()) {
            List<Feedback> reviews = feedbackService.getFeedbackForEvent(eventId);
            double avgRating = feedbackService.getAverageRating(eventId);
            long reviewCount = feedbackService.getReviewCount(eventId);

            model.addAttribute("event", event);
            model.addAttribute("reviews", reviews);
            model.addAttribute("avgRating", avgRating);
            model.addAttribute("reviewCount", reviewCount);
            return "event-feedback";
        }

        feedbackService.submitFeedback(feedback);
        redirectAttributes.addFlashAttribute("successMessage",
                "Thank you, " + feedback.getStudentName() + "! Your review and " + feedback.getRating() + "-star rating have been published.");

        return "redirect:/events/" + eventId + "/feedback";
    }

    // =========================================================
    // ADMIN VIEW EVENT FEEDBACK & SENTIMENT
    // =========================================================

    @GetMapping("/admin/events/{eventId}/feedback")
    public String adminEventFeedback(
            @PathVariable Long eventId,
            Model model) {

        Event event = eventService.getEventById(eventId);
        List<Feedback> reviews = feedbackService.getFeedbackForEvent(eventId);
        double avgRating = feedbackService.getAverageRating(eventId);
        long reviewCount = feedbackService.getReviewCount(eventId);

        model.addAttribute("event", event);
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgRating", avgRating);
        model.addAttribute("reviewCount", reviewCount);

        return "admin-event-feedback";
    }

    // =========================================================
    // ADMIN MODERATION: DELETE FEEDBACK
    // =========================================================

    @PostMapping("/admin/feedback/delete/{id}")
    public String deleteFeedback(
            @PathVariable Long id,
            @RequestParam Long eventId,
            RedirectAttributes redirectAttributes) {

        feedbackService.deleteFeedback(id);
        redirectAttributes.addFlashAttribute("successMessage", "Feedback entry removed successfully.");
        return "redirect:/admin/events/" + eventId + "/feedback";
    }
}
