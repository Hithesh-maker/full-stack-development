package com.campus.smartcampus.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

public class TeamRegistrationForm {

    private Long eventId;

    @NotBlank(message = "Team name is required")
    private String teamName;

    @NotBlank(message = "Team leader name is required")
    private String leaderName;

    @NotBlank(message = "Team leader email is required")
    @Email(message = "Valid leader email is required")
    private String leaderEmail;

    private String leaderDepartment;

    private List<MemberEntry> members = new ArrayList<>();

    private String paymentId;

    public TeamRegistrationForm() {
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public String getLeaderEmail() {
        return leaderEmail;
    }

    public void setLeaderEmail(String leaderEmail) {
        this.leaderEmail = leaderEmail;
    }

    public String getLeaderDepartment() {
        return leaderDepartment;
    }

    public void setLeaderDepartment(String leaderDepartment) {
        this.leaderDepartment = leaderDepartment;
    }

    public List<MemberEntry> getMembers() {
        return members;
    }

    public void setMembers(List<MemberEntry> members) {
        this.members = members;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public static class MemberEntry {
        private String name;
        private String email;
        private String department;

        public MemberEntry() {
        }

        public MemberEntry(String name, String email, String department) {
            this.name = name;
            this.email = email;
            this.department = department;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
    }
}
