package com.cloud.notification.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_events")
public class NotificationEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, length = 2000)
    private String messageBody;

    @Column(nullable = false)
    private String priority; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(nullable = false)
    private String channel;  // EMAIL, SMS, WEBHOOK

    @Column(nullable = false)
    private String dispatchStatus; // QUEUED, SENT, FAILED

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public NotificationEvent() {
    }

    public NotificationEvent(String recipient, String subject, String messageBody,
                             String priority, String channel, String dispatchStatus) {
        this.recipient = recipient;
        this.subject = subject;
        this.messageBody = messageBody;
        this.priority = priority;
        this.channel = channel;
        this.dispatchStatus = dispatchStatus;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDispatchStatus() {
        return dispatchStatus;
    }

    public void setDispatchStatus(String dispatchStatus) {
        this.dispatchStatus = dispatchStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
