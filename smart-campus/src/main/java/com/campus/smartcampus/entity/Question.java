package com.campus.smartcampus.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private String studentName;

    private String studentEmail;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionText;

    private Integer upvotes = 0;

    private LocalDateTime createdAt;

    private Boolean isAnswered = false;

    public Question() {
        this.createdAt = LocalDateTime.now();
        this.upvotes = 0;
        this.isAnswered = false;
    }

    public Question(Long eventId, String studentName, String studentEmail, String questionText) {
        this.eventId = eventId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.questionText = questionText;
        this.createdAt = LocalDateTime.now();
        this.upvotes = 0;
        this.isAnswered = false;
    }

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

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Integer getUpvotes() {
        return upvotes != null ? upvotes : 0;
    }

    public void setUpvotes(Integer upvotes) {
        this.upvotes = upvotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getIsAnswered() {
        return isAnswered != null ? isAnswered : false;
    }

    public void setIsAnswered(Boolean isAnswered) {
        this.isAnswered = isAnswered;
    }
}
