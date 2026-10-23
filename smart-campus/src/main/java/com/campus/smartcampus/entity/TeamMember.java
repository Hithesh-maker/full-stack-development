package com.campus.smartcampus.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "team_members")
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "department")
    private String department;

    @Column(name = "role", nullable = false, length = 32)
    private String role = "MEMBER";

    @Column(name = "registration_id")
    private Long registrationId;

    public TeamMember() {
    }

    public TeamMember(String studentName, String email, String department, String role, Long registrationId) {
        this.studentName = studentName;
        this.email = email;
        this.department = department;
        this.role = role;
        this.registrationId = registrationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }
}
