package com.campus.smartcampus.repository;

import com.campus.smartcampus.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // Retrieve feedback for a specific event sorted newest first
    List<Feedback> findByEventIdOrderBySubmittedAtDesc(Long eventId);

    // Aggregate function: Compute average rating for an event
    @Query("SELECT COALESCE(AVG(f.rating), 0.0) FROM Feedback f WHERE f.eventId = :eventId")
    Double getAverageRatingByEventId(@Param("eventId") Long eventId);

    // Total count of feedback submissions for an event
    long countByEventId(Long eventId);

    // Aggregate function: Overall campus events average satisfaction score
    @Query("SELECT COALESCE(AVG(f.rating), 0.0) FROM Feedback f")
    Double getOverallAverageRating();

    // Check if a student already submitted feedback for an event
    boolean existsByEventIdAndEmailIgnoreCase(Long eventId, String email);
}
