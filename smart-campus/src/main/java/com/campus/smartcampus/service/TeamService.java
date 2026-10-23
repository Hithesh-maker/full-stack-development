package com.campus.smartcampus.service;

import com.campus.smartcampus.dto.TeamRegistrationForm;
import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Registration;
import com.campus.smartcampus.entity.Team;
import com.campus.smartcampus.entity.TeamMember;
import com.campus.smartcampus.repository.EventRepository;
import com.campus.smartcampus.repository.RegistrationRepository;
import com.campus.smartcampus.repository.TeamMemberRepository;
import com.campus.smartcampus.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final NotificationService notificationService;

    public TeamService(
            TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository,
            EventRepository eventRepository,
            RegistrationRepository registrationRepository,
            NotificationService notificationService) {
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.notificationService = notificationService;
    }

    public Team registerTeam(TeamRegistrationForm form) {
        if (form == null || form.getEventId() == null) {
            throw new IllegalArgumentException("Registration form and event ID cannot be null.");
        }

        Event event = eventRepository.findById(form.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + form.getEventId()));

        if (event.isPast()) {
            throw new IllegalStateException("Registration is closed for completed campus events.");
        }

        // 1. Gather all candidates (Leader + additional members)
        List<TeamMemberInfo> memberList = new ArrayList<>();
        memberList.add(new TeamMemberInfo(
                form.getLeaderName(),
                form.getLeaderEmail() != null ? form.getLeaderEmail().trim().toLowerCase() : "",
                form.getLeaderDepartment(),
                "LEADER"
        ));

        if (form.getMembers() != null) {
            for (TeamRegistrationForm.MemberEntry entry : form.getMembers()) {
                if (entry.getName() != null && !entry.getName().isBlank() &&
                    entry.getEmail() != null && !entry.getEmail().isBlank()) {
                    memberList.add(new TeamMemberInfo(
                            entry.getName().trim(),
                            entry.getEmail().trim().toLowerCase(),
                            entry.getDepartment(),
                            "MEMBER"
                    ));
                }
            }
        }

        // 2. Validate Team Size
        int minSize = event.getMinTeamSize() != null ? event.getMinTeamSize() : 2;
        int maxSize = event.getMaxTeamSize() != null ? event.getMaxTeamSize() : 4;
        int teamSize = memberList.size();

        if (teamSize < minSize || teamSize > maxSize) {
            throw new IllegalArgumentException("Team size must be between " + minSize + " and " + maxSize +
                    " members. You supplied " + teamSize + ".");
        }

        // 3. Validate No Duplicate Emails Within The Team
        Set<String> emailSet = new HashSet<>();
        for (TeamMemberInfo m : memberList) {
            if (!emailSet.add(m.email)) {
                throw new IllegalArgumentException("Duplicate email detected within team: " + m.email);
            }
        }

        // 4. Validate Capacity
        Long currentBooked = registrationRepository.sumTicketsByEventId(event.getId());
        long booked = currentBooked != null ? currentBooked : 0L;
        if (booked + teamSize > event.getCapacity()) {
            long remaining = Math.max(0, event.getCapacity() - booked);
            throw new IllegalStateException("Capacity exceeded: Only " + remaining + " spots left, but team requires " + teamSize + ".");
        }

        // 5. Validate None Are Already Registered
        for (TeamMemberInfo m : memberList) {
            if (registrationRepository.existsActiveRegistration(event.getId(), m.email)) {
                throw new IllegalArgumentException("Member " + m.name + " (" + m.email + ") is already registered for this event.");
            }
        }

        // 6. Generate Team Code & Save Team
        String teamCode = "TEAM-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        Team team = new Team(
                form.getTeamName().trim(),
                event,
                teamCode,
                form.getLeaderName().trim(),
                form.getLeaderEmail().trim().toLowerCase(),
                form.getLeaderDepartment()
        );
        team = teamRepository.save(team);

        // 7. Create Individual Registration Passes & TeamMember Records
        for (TeamMemberInfo m : memberList) {
            Registration pass = new Registration();
            pass.setEvent(event);
            pass.setEventId(event.getId());
            pass.setStudentName(m.name);
            pass.setEmail(m.email);
            pass.setDepartment(m.department != null && !m.department.isBlank() ? m.department : event.getDepartment());
            pass.setTickets(1);
            pass.setStatus("CONFIRMED");
            pass.setRegisteredAt(LocalDateTime.now());
            pass.setToken(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            pass.setPaymentId(form.getPaymentId());
            pass.setPaymentAmount(event.isPaid() ? event.getPrice() : 0.0);

            Registration savedPass = registrationRepository.save(pass);

            // Send notification for each member pass
            try {
                notificationService.sendBookingConfirmation(savedPass);
            } catch (Exception ignored) {
            }

            TeamMember teamMember = new TeamMember(
                    m.name,
                    m.email,
                    m.department,
                    m.role,
                    savedPass.getId()
            );
            team.addMember(teamMember);
        }

        return teamRepository.save(team);
    }

    @Transactional(readOnly = true)
    public Optional<Team> getTeamByCode(String teamCode) {
        return teamRepository.findByTeamCode(teamCode);
    }

    @Transactional(readOnly = true)
    public List<TeamMember> getTeamMembers(Long teamId) {
        return teamMemberRepository.findByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public List<Team> getTeamsByEventId(Long eventId) {
        return teamRepository.findByEventId(eventId);
    }

    @Transactional(readOnly = true)
    public Optional<Team> getTeamForRegistration(Long registrationId) {
        return teamMemberRepository.findByRegistrationId(registrationId)
                .map(TeamMember::getTeam);
    }

    private static class TeamMemberInfo {
        String name;
        String email;
        String department;
        String role;

        TeamMemberInfo(String name, String email, String department, String role) {
            this.name = name;
            this.email = email;
            this.department = department;
            this.role = role;
        }
    }
}
