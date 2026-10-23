package com.cloud.notification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class NotificationRequest {

    @NotBlank(message = "Recipient cannot be blank")
    private String recipient;

    @NotBlank(message = "Subject cannot be blank")
    @Size(min = 3, max = 150, message = "Subject must be between 3 and 150 characters")
    private String subject;

    @NotBlank(message = "Message body cannot be blank")
    @Size(min = 5, max = 2000, message = "Message body must be between 5 and 2000 characters")
    private String messageBody;

    @NotBlank(message = "Priority is required")
    @Pattern(regexp = "^(LOW|MEDIUM|HIGH|CRITICAL)$", message = "Priority must be LOW, MEDIUM, HIGH, or CRITICAL")
    private String priority;

    @NotBlank(message = "Channel is required")
    @Pattern(regexp = "^(EMAIL|SMS|WEBHOOK)$", message = "Channel must be EMAIL, SMS, or WEBHOOK")
    private String channel;

    public NotificationRequest() {
    }

    public NotificationRequest(String recipient, String subject, String messageBody, String priority, String channel) {
        this.recipient = recipient;
        this.subject = subject;
        this.messageBody = messageBody;
        this.priority = priority;
        this.channel = channel;
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
}
