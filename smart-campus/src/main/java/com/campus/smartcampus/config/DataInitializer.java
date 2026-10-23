package com.campus.smartcampus.config;

import com.campus.smartcampus.entity.*;
import com.campus.smartcampus.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final FeedbackRepository feedbackRepository;
    private final RegistrationRepository registrationRepository;
    private final EventSessionRepository eventSessionRepository;
    private final CertificateRepository certificateRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;

    public DataInitializer(
            EventRepository eventRepository,
            FeedbackRepository feedbackRepository,
            RegistrationRepository registrationRepository,
            EventSessionRepository eventSessionRepository,
            CertificateRepository certificateRepository,
            NotificationLogRepository notificationLogRepository,
            TeamRepository teamRepository,
            TeamMemberRepository teamMemberRepository) {
        this.eventRepository = eventRepository;
        this.feedbackRepository = feedbackRepository;
        this.registrationRepository = registrationRepository;
        this.eventSessionRepository = eventSessionRepository;
        this.certificateRepository = certificateRepository;
        this.notificationLogRepository = notificationLogRepository;
        this.teamRepository = teamRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    @Override
    public void run(String... args) {
        LocalDate today = LocalDate.now();

        if (eventRepository.count() < 3) {
            Event event1 = new Event();
            event1.setTitle("AI & Machine Learning Hackathon 2026");
            event1.setDescription("A 24-hour national hackathon challenging students to build real-world AI and computer vision solutions. Mentors from top tech companies will be present.");
            event1.setDepartment("Computer Science and Engineering");
            event1.setDate(today.plusDays(7));
            event1.setTime(LocalTime.of(9, 30));
            event1.setVenue("Tech Hub Auditorium & Innovation Lab");
            event1.setType("Hackathon");
            event1.setCapacity(60);
            event1.setPrice(150.0);
            event1.setIsPaid(true);
            event1.setRegistrationType("TEAM");
            event1.setMinTeamSize(2);
            event1.setMaxTeamSize(4);
            eventRepository.save(event1);

            Event event2 = new Event();
            event2.setTitle("Full Stack Cloud & DevOps Hands-on Workshop");
            event2.setDescription("Learn to containerize applications with Docker, deploy microservices on Kubernetes, and set up continuous integration pipelines on Google Cloud.");
            event2.setDepartment("Information Technology");
            event2.setDate(today.plusDays(12));
            event2.setTime(LocalTime.of(10, 0));
            event2.setVenue("Computing Center - Lab 4");
            event2.setType("Workshop");
            event2.setCapacity(45);
            event2.setPrice(0.0);
            event2.setIsPaid(false);
            event2.setRegistrationType("INDIVIDUAL");
            eventRepository.save(event2);

            Event event3 = new Event();
            event3.setTitle("National Robotics & IoT Championship");
            event3.setDescription("Showcase autonomous bots, line followers, and smart campus IoT sensors in head-to-head competitive arenas with exciting cash prizes.");
            event3.setDepartment("Electronics and Communication Engineering");
            event3.setDate(today.plusDays(18));
            event3.setTime(LocalTime.of(11, 0));
            event3.setVenue("Indoor Sports Complex Arena");
            event3.setType("Competition");
            event3.setCapacity(50);
            event3.setPrice(0.0);
            event3.setIsPaid(false);
            event3.setRegistrationType("INDIVIDUAL");
            eventRepository.save(event3);

            Event event4 = new Event();
            event4.setTitle("Cybersecurity & Ethical Hacking Seminar");
            event4.setDescription("Industry leaders discuss modern cyber defense, penetration testing fundamentals, zero-trust architectures, and career paths in information security.");
            event4.setDepartment("Computer Science and Engineering");
            event4.setDate(today.plusDays(24));
            event4.setTime(LocalTime.of(14, 0));
            event4.setVenue("Seminar Hall 2");
            event4.setType("Seminar");
            event4.setCapacity(80);
            event4.setPrice(0.0);
            event4.setIsPaid(false);
            event4.setRegistrationType("INDIVIDUAL");
            eventRepository.save(event4);

            Event event5 = new Event();
            event5.setTitle("Smart Campus Annual Cultural Night 2026");
            event5.setDescription("An evening of vibrant music, drama, battle of the bands, and dance performances celebrating campus diversity and student talents.");
            event5.setDepartment("Cultural Committee");
            event5.setDate(today.plusDays(30));
            event5.setTime(LocalTime.of(17, 30));
            event5.setVenue("Open Air Amphitheatre");
            event5.setType("Cultural");
            event5.setCapacity(250);
            event5.setPrice(0.0);
            event5.setIsPaid(false);
            event5.setRegistrationType("INDIVIDUAL");
            eventRepository.save(event5);
        }

        // Backfill events with null price, isPaid, or registrationType
        List<Event> allEvents = eventRepository.findAll();
        for (Event e : allEvents) {
            boolean changed = false;
            if (e.getIsPaid() == null) {
                e.setIsPaid(e.getPrice() != null && e.getPrice() > 0);
                changed = true;
            }
            if (e.getPrice() == null) {
                e.setPrice(0.0);
                changed = true;
            }
            if (e.getRegistrationType() == null || e.getRegistrationType().isBlank()) {
                if (e.getTitle() != null && e.getTitle().toLowerCase().contains("hackathon")) {
                    e.setRegistrationType("TEAM");
                    e.setMinTeamSize(2);
                    e.setMaxTeamSize(4);
                } else {
                    e.setRegistrationType("INDIVIDUAL");
                    e.setMinTeamSize(1);
                    e.setMaxTeamSize(1);
                }
                changed = true;
            }
            if (changed) {
                eventRepository.save(e);
            }
        }

        // Seed Event Agenda Sessions for Event 1 (AI Hackathon)
        List<Event> currentEvents = eventRepository.findAll();
        if (!currentEvents.isEmpty()) {
            Event hackathon = currentEvents.get(0);
            if (eventSessionRepository.countByEventId(hackathon.getId()) == 0) {
                eventSessionRepository.save(new EventSession(hackathon, "Opening Keynote & Problem Statements", LocalTime.of(9, 30), LocalTime.of(10, 30), "Dr. S. Ramanathan (Director AI)", "Auditorium Main Stage", "KEYNOTE", 1));
                eventSessionRepository.save(new EventSession(hackathon, "Sprint 1: Architecture & Model Prototyping", LocalTime.of(10, 30), LocalTime.of(13, 0), "Cloud AI Mentors", "Innovation Lab A", "WORKSHOP", 2));
                eventSessionRepository.save(new EventSession(hackathon, "Networking Lunch & Refreshments", LocalTime.of(13, 0), LocalTime.of(14, 0), "Campus Hospitality", "Dining Courtyard", "BREAK", 3));
                eventSessionRepository.save(new EventSession(hackathon, "Sprint 2: Full-Stack Integration & Stress Testing", LocalTime.of(14, 0), LocalTime.of(17, 0), "DevOps Mentors", "Innovation Lab B", "WORKSHOP", 4));
                eventSessionRepository.save(new EventSession(hackathon, "Final Project Demonstrations & Jury Review", LocalTime.of(17, 0), LocalTime.of(19, 0), "Distinguished Faculty Jury", "Auditorium Main Stage", "PANEL", 5));
            }
        }

        // Seed Past Event with Verified Certificate for instant portal demo
        if (eventRepository.findAll().stream().noneMatch(e -> e.getTitle().contains("Scientific Computing"))) {
            Event pastEvent = new Event();
            pastEvent.setTitle("Advanced Python & Scientific Computing Masterclass");
            pastEvent.setDescription("Intensive bootcamp covering NumPy, Pandas, scikit-learn, and production machine learning workflows.");
            pastEvent.setDepartment("Computer Science and Engineering");
            pastEvent.setDate(today.minusDays(4));
            pastEvent.setTime(LocalTime.of(10, 0));
            pastEvent.setVenue("Computing Center - Lab 1");
            pastEvent.setType("Workshop");
            pastEvent.setCapacity(50);
            pastEvent.setPrice(0.0);
            pastEvent.setIsPaid(false);
            pastEvent.setRegistrationType("INDIVIDUAL");
            pastEvent = eventRepository.save(pastEvent);

            Registration certReg = new Registration();
            certReg.setStudentName("Ananya Iyer");
            certReg.setEmail("ananya.iyer@campus.edu");
            certReg.setDepartment("Computer Science and Engineering");
            certReg.setEventId(pastEvent.getId());
            certReg.setTickets(1);
            certReg.setStatus("CHECKED_IN");
            certReg.setToken("SC-EPY-ANANYA");
            certReg.setCheckedInAt(LocalDateTime.now().minusDays(4).plusHours(2));
            certReg = registrationRepository.save(certReg);

            Certificate demoCert = new Certificate(
                    "CERT-AI-2026-001",
                    "SC-CERT-2026-001",
                    "Ananya Iyer",
                    "ananya.iyer@campus.edu",
                    pastEvent.getTitle(),
                    pastEvent.getDepartment(),
                    pastEvent.getDate(),
                    certReg.getId(),
                    pastEvent.getId()
            );
            certificateRepository.save(demoCert);
        }

        // Seed Demo Team "ByteRunners" for Event 1 if not exists
        if (teamRepository.findByTeamCode("TEAM-BYTE-01").isEmpty() && !currentEvents.isEmpty()) {
            Event teamEvent = currentEvents.get(0);
            Team team = new Team("ByteRunners", teamEvent, "TEAM-BYTE-01", "Rahul Sharma", "rahul.sharma@campus.edu", "Computer Science and Engineering");
            team = teamRepository.save(team);

            Registration r1 = new Registration();
            r1.setStudentName("Rahul Sharma");
            r1.setEmail("rahul.sharma@campus.edu");
            r1.setDepartment("Computer Science and Engineering");
            r1.setEventId(teamEvent.getId());
            r1.setTickets(1);
            r1.setStatus("CONFIRMED");
            r1.setToken("SC-E1-RAHUL");
            r1 = registrationRepository.save(r1);
            TeamMember m1 = new TeamMember("Rahul Sharma", "rahul.sharma@campus.edu", "Computer Science and Engineering", "LEADER", r1.getId());
            m1.setTeam(team);
            teamMemberRepository.save(m1);

            Registration r2 = new Registration();
            r2.setStudentName("Neha Patel");
            r2.setEmail("neha.patel@campus.edu");
            r2.setDepartment("Computer Science and Engineering");
            r2.setEventId(teamEvent.getId());
            r2.setTickets(1);
            r2.setStatus("CONFIRMED");
            r2.setToken("SC-E1-NEHA");
            r2 = registrationRepository.save(r2);
            TeamMember m2 = new TeamMember("Neha Patel", "neha.patel@campus.edu", "Computer Science and Engineering", "MEMBER", r2.getId());
            m2.setTeam(team);
            teamMemberRepository.save(m2);

            Registration r3 = new Registration();
            r3.setStudentName("Vikramaditya Rao");
            r3.setEmail("vikram.rao@campus.edu");
            r3.setDepartment("Information Technology");
            r3.setEventId(teamEvent.getId());
            r3.setTickets(1);
            r3.setStatus("CONFIRMED");
            r3.setToken("SC-E1-VIKRAM");
            r3 = registrationRepository.save(r3);
            TeamMember m3 = new TeamMember("Vikramaditya Rao", "vikram.rao@campus.edu", "Information Technology", "MEMBER", r3.getId());
            m3.setTeam(team);
            teamMemberRepository.save(m3);
        }

        // Backfill registrations with missing token
        var allRegistrations = registrationRepository.findAll();
        for (var r : allRegistrations) {
            if (r.getToken() == null || r.getToken().isBlank()) {
                r.setToken(r.generateToken());
                registrationRepository.save(r);
            }
        }

        // Seed Multi-Channel Notification Logs if empty
        if (notificationLogRepository.count() == 0) {
            notificationLogRepository.save(new NotificationLog(
                    "rahul.sharma@campus.edu", "Rahul Sharma",
                    "Official Admission Pass: AI & Machine Learning Hackathon 2026",
                    "CONFIRMATION",
                    "<p>Your admission pass is confirmed. Token: <strong>SC-E1-RAHUL</strong></p>",
                    "EMAIL"
            ));
            notificationLogRepository.save(new NotificationLog(
                    "rahul.sharma@campus.edu", "Rahul Sharma",
                    "WhatsApp Pass Dispatch: AI & Machine Learning Hackathon 2026",
                    "CONFIRMATION",
                    "Smart Campus Pass for Rahul Sharma. Token: SC-E1-RAHUL. Venue: Tech Hub Auditorium.",
                    "WHATSAPP"
            ));
            notificationLogRepository.save(new NotificationLog(
                    "rahul.sharma@campus.edu", "Rahul Sharma",
                    "SMS Pass Alert: AI & Machine Learning Hackathon 2026",
                    "CONFIRMATION",
                    "SC-PASS: Rahul Sharma, Token SC-E1-RAHUL confirmed for AI Hackathon 2026.",
                    "SMS"
            ));
            notificationLogRepository.save(new NotificationLog(
                    "ananya.iyer@campus.edu", "Ananya Iyer",
                    "Verified Certificate Available: Advanced Python Masterclass",
                    "CERTIFICATE",
                    "<p>Your verified certificate CERT-AI-2026-001 has been issued. View at /verify/certificate/CERT-AI-2026-001</p>",
                    "EMAIL"
            ));
            notificationLogRepository.save(new NotificationLog(
                    "ananya.iyer@campus.edu", "Ananya Iyer",
                    "WhatsApp Certificate Notice: Advanced Python Masterclass",
                    "CERTIFICATE",
                    "Congratulations Ananya! Your Certificate of Completion (CERT-AI-2026-001) is verified.",
                    "WHATSAPP"
            ));
        }

        // Seed student reviews with rich sentiment and keywords if empty
        if (feedbackRepository.count() < 4) {
            List<Event> events = eventRepository.findAll();
            if (!events.isEmpty()) {
                Event e1 = events.get(0);
                feedbackRepository.save(new Feedback(
                        e1.getId(),
                        "Aarav Sharma",
                        "aarav.sharma@campus.edu",
                        5,
                        "Incredible hands-on hackathon experience! Mentors provided insightful guidance on deep learning architectures, practical examples, and excellent organization."
                ));
                feedbackRepository.save(new Feedback(
                        e1.getId(),
                        "Priya Venkatesh",
                        "priya.v@campus.edu",
                        4,
                        "Very well organized workshop with insightful demonstrations. Good speaker delivery and practical exercises throughout."
                ));

                if (events.size() > 1) {
                    Event e2 = events.get(1);
                    feedbackRepository.save(new Feedback(
                            e2.getId(),
                            "Kavya Nair",
                            "kavya.nair@campus.edu",
                            5,
                            "Outstanding hands-on session. The Docker and Kubernetes deployment exercises were practical, insightful, and well explained by the speaker."
                    ));
                    feedbackRepository.save(new Feedback(
                            e2.getId(),
                            "Rohan Deshmukh",
                            "rohan.d@campus.edu",
                            3,
                            "Decent content and informative speaker, but the lab was crowded and network timing could be improved."
                    ));
                }
            }
        }
    }
}
