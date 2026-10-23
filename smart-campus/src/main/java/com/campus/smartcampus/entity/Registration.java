package com.campus.smartcampus.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Student name is required")
    private String studentName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Department is required")
    private String department;

    @NotNull(message = "Event ID is required")
    @Column(name = "event_id")
    private Long eventId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", insertable = false, updatable = false)
    private Event event;

    @NotNull(message = "Number of tickets is required")
    private Integer tickets;

    private java.time.LocalDateTime registeredAt;

    private String status = "CONFIRMED";

    @Column(name = "token", length = 64)
    private String token;

    private java.time.LocalDateTime checkedInAt;

    private Integer waitlistPosition;

    private String paymentId;

    private Double paymentAmount = 0.0;

    @PrePersist
    public void prePersist() {
        if (this.registeredAt == null) {
            this.registeredAt = java.time.LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = "CONFIRMED";
        }
        if (this.token == null || this.token.isBlank()) {
            this.token = generateToken();
        }
    }

    public String generateToken() {
        String eventPart = this.eventId != null ? String.valueOf(this.eventId) : "0";
        String randomSuffix = java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "SC-E" + eventPart + "-" + randomSuffix;
    }

    public boolean isConfirmed() {
        return "CONFIRMED".equalsIgnoreCase(this.status);
    }

    public boolean isWaitlisted() {
        return "WAITLISTED".equalsIgnoreCase(this.status);
    }

    public boolean isCheckedIn() {
        return "CHECKED_IN".equalsIgnoreCase(this.status);
    }

    public boolean isCancelled() {
        return "CANCELLED".equalsIgnoreCase(this.status);
    }

    public boolean canDownloadCertificate() {
        return isCheckedIn() || (isConfirmed() && event != null && event.isPast());
    }

    public Registration() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Integer getTickets() {
        return tickets;
    }

    public void setTickets(Integer tickets) {
        this.tickets = tickets;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
        if (event != null && event.getId() != null) {
            this.eventId = event.getId();
        }
    }

    public java.time.LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(java.time.LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public java.time.LocalDateTime getCheckedInAt() {
        return checkedInAt;
    }

    public void setCheckedInAt(java.time.LocalDateTime checkedInAt) {
        this.checkedInAt = checkedInAt;
    }

    public Integer getWaitlistPosition() {
        return waitlistPosition;
    }

    public void setWaitlistPosition(Integer waitlistPosition) {
        this.waitlistPosition = waitlistPosition;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Double getPaymentAmount() {
        return paymentAmount != null ? paymentAmount : 0.0;
    }

    public void setPaymentAmount(Double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }
}