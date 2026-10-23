package com.campus.smartcampus.service;

import com.campus.smartcampus.dto.TeamRegistrationForm;
import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Registration;
import com.campus.smartcampus.entity.Team;
import com.campus.smartcampus.repository.EventRepository;
import com.campus.smartcampus.repository.RegistrationRepository;
import com.campus.smartcampus.repository.TeamMemberRepository;
import com.campus.smartcampus.repository.TeamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TeamService teamService;

    private Event mockEvent;

    @BeforeEach
    void setUp() {
        mockEvent = new Event();
        mockEvent.setId(20L);
        mockEvent.setTitle("Campus Hackathon 2026");
        mockEvent.setType("Hackathon");
        mockEvent.setDepartment("Computer Science and Engineering");
        mockEvent.setCapacity(50);
        mockEvent.setDate(LocalDate.now().plusDays(10));
        mockEvent.setTime(LocalTime.of(9, 0));
        mockEvent.setMinTeamSize(2);
        mockEvent.setMaxTeamSize(4);
    }

    @Test
    @DisplayName("registerTeam should successfully create team and individual passes")
    void testRegisterTeam_Success() {
        TeamRegistrationForm form = new TeamRegistrationForm();
        form.setEventId(20L);
        form.setTeamName("CyberTitans");
        form.setLeaderName("Alice Smith");
        form.setLeaderEmail("alice@campus.edu");
        form.setLeaderDepartment("Computer Science and Engineering");

        List<TeamRegistrationForm.MemberEntry> members = new ArrayList<>();
        members.add(new TeamRegistrationForm.MemberEntry("Bob Jones", "bob@campus.edu", "Information Technology"));
        members.add(new TeamRegistrationForm.MemberEntry("Charlie Day", "charlie@campus.edu", "Computer Science and Engineering"));
        form.setMembers(members);

        when(eventRepository.findById(20L)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.sumTicketsByEventId(20L)).thenReturn(10L);
        when(registrationRepository.existsActiveRegistration(eq(20L), anyString())).thenReturn(false);

        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> {
            Team t = invocation.getArgument(0);
            t.setId(100L);
            return t;
        });

        when(registrationRepository.save(any(Registration.class))).thenAnswer(invocation -> {
            Registration r = invocation.getArgument(0);
            r.setId(500L);
            return r;
        });

        Team created = teamService.registerTeam(form);

        assertNotNull(created);
        assertEquals("CyberTitans", created.getTeamName());
        assertTrue(created.getTeamCode().startsWith("TEAM-"));
        assertEquals("Alice Smith", created.getLeadName());
        assertEquals(3, created.getMembers().size());

        // Verify individual registration passes created for each of the 3 members
        verify(registrationRepository, times(3)).save(any(Registration.class));
        verify(teamRepository, times(2)).save(any(Team.class));
    }

    @Test
    @DisplayName("registerTeam should throw when team size is below minimum")
    void testRegisterTeam_BelowMinimumSize() {
        TeamRegistrationForm form = new TeamRegistrationForm();
        form.setEventId(20L);
        form.setTeamName("Solo Squad");
        form.setLeaderName("Solo Joe");
        form.setLeaderEmail("joe@campus.edu");
        form.setMembers(new ArrayList<>()); // 0 additional members => total 1

        when(eventRepository.findById(20L)).thenReturn(Optional.of(mockEvent));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> teamService.registerTeam(form));
        assertTrue(ex.getMessage().contains("Team size must be between"));
    }

    @Test
    @DisplayName("registerTeam should throw when team size exceeds maximum")
    void testRegisterTeam_AboveMaximumSize() {
        TeamRegistrationForm form = new TeamRegistrationForm();
        form.setEventId(20L);
        form.setTeamName("Mega Squad");
        form.setLeaderName("Leader");
        form.setLeaderEmail("leader@campus.edu");

        List<TeamRegistrationForm.MemberEntry> members = new ArrayList<>();
        members.add(new TeamRegistrationForm.MemberEntry("M1", "m1@campus.edu", "CSE"));
        members.add(new TeamRegistrationForm.MemberEntry("M2", "m2@campus.edu", "CSE"));
        members.add(new TeamRegistrationForm.MemberEntry("M3", "m3@campus.edu", "CSE"));
        members.add(new TeamRegistrationForm.MemberEntry("M4", "m4@campus.edu", "CSE")); // 4 + 1 = 5 members
        form.setMembers(members);

        when(eventRepository.findById(20L)).thenReturn(Optional.of(mockEvent));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> teamService.registerTeam(form));
        assertTrue(ex.getMessage().contains("Team size must be between"));
    }

    @Test
    @DisplayName("registerTeam should throw when team contains duplicate emails")
    void testRegisterTeam_DuplicateEmail() {
        TeamRegistrationForm form = new TeamRegistrationForm();
        form.setEventId(20L);
        form.setTeamName("Dup Squad");
        form.setLeaderName("Alice");
        form.setLeaderEmail("same@campus.edu");

        List<TeamRegistrationForm.MemberEntry> members = new ArrayList<>();
        members.add(new TeamRegistrationForm.MemberEntry("Bob", "same@campus.edu", "CSE"));
        form.setMembers(members);

        when(eventRepository.findById(20L)).thenReturn(Optional.of(mockEvent));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> teamService.registerTeam(form));
        assertTrue(ex.getMessage().contains("Duplicate email detected"));
    }

    @Test
    @DisplayName("registerTeam should throw when member is already registered in event")
    void testRegisterTeam_MemberAlreadyRegistered() {
        TeamRegistrationForm form = new TeamRegistrationForm();
        form.setEventId(20L);
        form.setTeamName("CyberSquad");
        form.setLeaderName("Alice");
        form.setLeaderEmail("alice@campus.edu");

        List<TeamRegistrationForm.MemberEntry> members = new ArrayList<>();
        members.add(new TeamRegistrationForm.MemberEntry("Bob", "bob@campus.edu", "CSE"));
        form.setMembers(members);

        when(eventRepository.findById(20L)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.sumTicketsByEventId(20L)).thenReturn(5L);
        when(registrationRepository.existsActiveRegistration(20L, "alice@campus.edu")).thenReturn(false);
        when(registrationRepository.existsActiveRegistration(20L, "bob@campus.edu")).thenReturn(true);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> teamService.registerTeam(form));
        assertTrue(ex.getMessage().contains("already registered"));
    }

    @Test
    @DisplayName("registerTeam should throw when capacity is insufficient")
    void testRegisterTeam_CapacityExceeded() {
        TeamRegistrationForm form = new TeamRegistrationForm();
        form.setEventId(20L);
        form.setTeamName("CyberSquad");
        form.setLeaderName("Alice");
        form.setLeaderEmail("alice@campus.edu");

        List<TeamRegistrationForm.MemberEntry> members = new ArrayList<>();
        members.add(new TeamRegistrationForm.MemberEntry("Bob", "bob@campus.edu", "CSE"));
        form.setMembers(members); // 2 members

        when(eventRepository.findById(20L)).thenReturn(Optional.of(mockEvent));
        when(registrationRepository.sumTicketsByEventId(20L)).thenReturn(49L); // 49 / 50 booked

        Exception ex = assertThrows(IllegalStateException.class, () -> teamService.registerTeam(form));
        assertTrue(ex.getMessage().contains("Capacity exceeded"));
    }
}
