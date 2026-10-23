package com.campus.smartcampus.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipientEmail;

    private String studentName;

    private String subject;

    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String bodyHtml;

    private LocalDateTime sentAt;

    private String status = "DELIVERED";

    @Column(name = "channel", length = 32)
    private String channel = "EMAIL"; // EMAIL, WHATSAPP, SMS

    public NotificationLog() {
        this.sentAt = LocalDateTime.now();
        this.channel = "EMAIL";
    }

    public NotificationLog(String recipientEmail, String studentName, String subject, String eventType, String bodyHtml) {
        this(recipientEmail, studentName, subject, eventType, bodyHtml, "EMAIL");
    }

    public NotificationLog(String recipientEmail, String studentName, String subject, String eventType, String bodyHtml, String channel) {
        this.recipientEmail = recipientEmail;
        this.studentName = studentName;
        this.subject = subject;
        this.eventType = eventType;
        this.bodyHtml = bodyHtml;
        this.sentAt = LocalDateTime.now();
        this.status = "DELIVERED";
        this.channel = channel != null ? channel : "EMAIL";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChannel() {
        return channel != null ? channel : "EMAIL";
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
