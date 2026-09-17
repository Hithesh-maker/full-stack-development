package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Feedback;
import com.campus.smartcampus.repository.EventRepository;
import com.campus.smartcampus.repository.FeedbackRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    private Event testEvent;
    private Feedback testFeedback;

    @BeforeEach
    void setUp() {
        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Machine Learning Bootcamp");

        testFeedback = new Feedback();
        testFeedback.setId(10L);
        testFeedback.setEventId(1L);
        testFeedback.setStudentName("Tanvi Patel");
        testFeedback.setEmail("tanvi.p@campus.edu");
        testFeedback.setRating(5);
        testFeedback.setComments("Fantastic hands-on workshop! Learned a lot about neural network architectures.");
    }

    @Test
    void testSubmitFeedback_Success() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(testFeedback);

        Feedback saved = feedbackService.submitFeedback(testFeedback);

        assertNotNull(saved);
        assertEquals(5, saved.getRating());
        assertEquals("Tanvi Patel", saved.getStudentName());
        verify(feedbackRepository, times(1)).save(testFeedback);
    }

    @Test
    void testSubmitFeedback_EventNotFound() {
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());
        testFeedback.setEventId(999L);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> feedbackService.submitFeedback(testFeedback));

        assertTrue(exception.getMessage().contains("Event not found with ID: 999"));
    }

    @Test
    void testSubmitFeedback_InvalidRating_TooHigh() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        testFeedback.setRating(6);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> feedbackService.submitFeedback(testFeedback));

        assertTrue(exception.getMessage().contains("Rating must be an integer between 1 and 5"));
    }

    @Test
    void testSubmitFeedback_InvalidRating_TooLow() {
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));
        testFeedback.setRating(0);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> feedbackService.submitFeedback(testFeedback));

        assertTrue(exception.getMessage().contains("Rating must be an integer between 1 and 5"));
    }

    @Test
    void testGetAverageRating_CalculatesCorrectly() {
        when(feedbackRepository.getAverageRatingByEventId(1L)).thenReturn(4.666666);

        double avg = feedbackService.getAverageRating(1L);

        assertEquals(4.7, avg, 0.01);
    }

    @Test
    void testGetAverageRating_NullHandled() {
        when(feedbackRepository.getAverageRatingByEventId(1L)).thenReturn(null);

        double avg = feedbackService.getAverageRating(1L);

        assertEquals(0.0, avg);
    }

    @Test
    void testGetFeedbackForEvent() {
        when(feedbackRepository.findByEventIdOrderBySubmittedAtDesc(1L))
                .thenReturn(List.of(testFeedback));

        List<Feedback> result = feedbackService.getFeedbackForEvent(1L);

        assertEquals(1, result.size());
        assertEquals("Tanvi Patel", result.get(0).getStudentName());
    }

    @Test
    void testGetReviewCount() {
        when(feedbackRepository.countByEventId(1L)).thenReturn(5L);

        long count = feedbackService.getReviewCount(1L);

        assertEquals(5L, count);
    }

    @Test
    void testDeleteFeedback() {
        doNothing().when(feedbackRepository).deleteById(10L);

        feedbackService.deleteFeedback(10L);

        verify(feedbackRepository, times(1)).deleteById(10L);
    }
}
