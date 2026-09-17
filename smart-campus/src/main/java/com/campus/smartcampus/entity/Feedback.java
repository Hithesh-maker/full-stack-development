package com.campus.smartcampus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Event ID is required")
    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @NotBlank(message = "Student name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(name = "student_name", nullable = false)
    private String studentName;

    @NotBlank(message = "Student email is required")
    @Email(message = "Please provide a valid campus email address")
    @Column(name = "email", nullable = false)
    private String email;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1 star")
    @Max(value = 5, message = "Rating cannot exceed 5 stars")
    @Column(nullable = false)
    private Integer rating;

    @NotBlank(message = "Feedback comments are required")
    @Size(min = 5, max = 1000, message = "Comments must be between 5 and 1000 characters")
    @Column(length = 1000, nullable = false)
    private String comments;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", insertable = false, updatable = false)
    private Event event;

    public Feedback() {
    }

    public Feedback(Long eventId, String studentName, String email, Integer rating, String comments) {
        this.eventId = eventId;
        this.studentName = studentName;
        this.email = email;
        this.rating = rating;
        this.comments = comments;
        this.submittedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Transient
    public String getStarsDisplay() {
        if (rating == null || rating < 1) return "☆☆☆☆☆";
        int r = Math.min(5, Math.max(1, rating));
        return "★".repeat(r) + "☆".repeat(5 - r);
    }
}
