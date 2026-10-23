package com.campus.smartcampus.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_name", nullable = false)
    private String teamName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "team_code", nullable = false, unique = true, length = 32)
    private String teamCode;

    @Column(name = "lead_name", nullable = false)
    private String leadName;

    @Column(name = "lead_email", nullable = false)
    private String leadEmail;

    @Column(name = "lead_department")
    private String leadDepartment;

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TeamMember> members = new ArrayList<>();

    public Team() {
        this.registeredAt = LocalDateTime.now();
    }

    public Team(String teamName, Event event, String teamCode, String leadName, String leadEmail, String leadDepartment) {
        this.teamName = teamName;
        this.event = event;
        this.teamCode = teamCode;
        this.leadName = leadName;
        this.leadEmail = leadEmail;
        this.leadDepartment = leadDepartment;
        this.registeredAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getTeamCode() {
        return teamCode;
    }

    public void setTeamCode(String teamCode) {
        this.teamCode = teamCode;
    }

    public String getLeadName() {
        return leadName;
    }

    public void setLeadName(String leadName) {
        this.leadName = leadName;
    }

    public String getLeadEmail() {
        return leadEmail;
    }

    public void setLeadEmail(String leadEmail) {
        this.leadEmail = leadEmail;
    }

    public String getLeadDepartment() {
        return leadDepartment;
    }

    public void setLeadDepartment(String leadDepartment) {
        this.leadDepartment = leadDepartment;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public List<TeamMember> getMembers() {
        return members;
    }

    public void setMembers(List<TeamMember> members) {
        this.members = members;
    }

    public void addMember(TeamMember member) {
        members.add(member);
        member.setTeam(this);
    }
}
