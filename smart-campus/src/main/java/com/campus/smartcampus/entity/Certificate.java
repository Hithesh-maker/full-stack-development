package com.campus.smartcampus.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "verification_code", nullable = false, unique = true, length = 64)
    private String verificationCode;

    @Column(name = "certificate_number", nullable = false, length = 64)
    private String certificateNumber;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "student_email", nullable = false)
    private String studentEmail;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @Column(name = "department", nullable = false)
    private String department;

    @Column(name = "event_date")
    private LocalDate eventDate;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "status", nullable = false, length = 32)
    private String status = "VALID";

    @Column(name = "registration_id")
    private Long registrationId;

    @Column(name = "event_id")
    private Long eventId;

    public Certificate() {
        this.issuedAt = LocalDateTime.now();
        this.status = "VALID";
    }

    public Certificate(String verificationCode, String certificateNumber, String studentName,
                       String studentEmail, String eventName, String department,
                       LocalDate eventDate, Long registrationId, Long eventId) {
        this.verificationCode = verificationCode;
        this.certificateNumber = certificateNumber;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.eventName = eventName;
        this.department = department;
        this.eventDate = eventDate;
        this.registrationId = registrationId;
        this.eventId = eventId;
        this.issuedAt = LocalDateTime.now();
        this.status = "VALID";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public String getCertificateNumber() {
        return certificateNumber;
    }

    public void setCertificateNumber(String certificateNumber) {
        this.certificateNumber = certificateNumber;
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

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public boolean isValid() {
        return "VALID".equalsIgnoreCase(this.status);
    }
}
