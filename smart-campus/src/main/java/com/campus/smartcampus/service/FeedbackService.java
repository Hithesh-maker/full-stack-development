package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Feedback;
import com.campus.smartcampus.repository.EventRepository;
import com.campus.smartcampus.repository.FeedbackRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final EventRepository eventRepository;

    public FeedbackService(FeedbackRepository feedbackRepository, EventRepository eventRepository) {
        this.feedbackRepository = feedbackRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public Feedback submitFeedback(Feedback feedback) {
        if (feedback.getEventId() == null) {
            throw new IllegalArgumentException("Event ID is required to submit feedback.");
        }

        // Validate event existence
        Event event = eventRepository.findById(feedback.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + feedback.getEventId()));

        if (feedback.getRating() == null || feedback.getRating() < 1 || feedback.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be an integer between 1 and 5 stars.");
        }

        if (feedback.getStudentName() != null) {
            feedback.setStudentName(feedback.getStudentName().trim());
        }
        if (feedback.getEmail() != null) {
            feedback.setEmail(feedback.getEmail().trim().toLowerCase());
        }
        if (feedback.getComments() != null) {
            feedback.setComments(feedback.getComments().trim());
        }

        feedback.setSubmittedAt(LocalDateTime.now());
        return feedbackRepository.save(feedback);
    }

    public List<Feedback> getFeedbackForEvent(Long eventId) {
        return feedbackRepository.findByEventIdOrderBySubmittedAtDesc(eventId);
    }

    public double getAverageRating(Long eventId) {
        Double avg = feedbackRepository.getAverageRatingByEventId(eventId);
        if (avg == null || avg == 0.0) {
            return 0.0;
        }
        // Round to 1 decimal place
        return Math.round(avg * 10.0) / 10.0;
    }

    public long getReviewCount(Long eventId) {
        return feedbackRepository.countByEventId(eventId);
    }

    public double getOverallAverageRating() {
        Double avg = feedbackRepository.getOverallAverageRating();
        if (avg == null || avg == 0.0) {
            return 0.0;
        }
        return Math.round(avg * 10.0) / 10.0;
    }

    public boolean hasStudentReviewed(Long eventId, String email) {
        if (eventId == null || email == null || email.isBlank()) {
            return false;
        }
        return feedbackRepository.existsByEventIdAndEmailIgnoreCase(eventId, email.trim());
    }

    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }

    @Transactional
    public void deleteFeedback(Long id) {
        feedbackRepository.deleteById(id);
    }
}
