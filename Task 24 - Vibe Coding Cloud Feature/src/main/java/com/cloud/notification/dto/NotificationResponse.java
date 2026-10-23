package com.cloud.notification.dto;

import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private String recipient;
    private String subject;
    private String priority;
    private String channel;
    private String dispatchStatus;
    private LocalDateTime createdAt;
    private String message;

    public NotificationResponse() {
    }

    public NotificationResponse(Long id, String recipient, String subject, String priority,
                                String channel, String dispatchStatus, LocalDateTime createdAt, String message) {
        this.id = id;
        this.recipient = recipient;
        this.subject = subject;
        this.priority = priority;
        this.channel = channel;
        this.dispatchStatus = dispatchStatus;
        this.createdAt = createdAt;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
