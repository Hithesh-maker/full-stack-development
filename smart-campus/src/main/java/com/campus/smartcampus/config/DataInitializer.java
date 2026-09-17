package com.campus.smartcampus.config;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Feedback;
import com.campus.smartcampus.repository.EventRepository;
import com.campus.smartcampus.repository.FeedbackRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final FeedbackRepository feedbackRepository;

    public DataInitializer(EventRepository eventRepository, FeedbackRepository feedbackRepository) {
        this.eventRepository = eventRepository;
        this.feedbackRepository = feedbackRepository;
    }

    @Override
    public void run(String... args) {
        if (eventRepository.count() < 3) {
            LocalDate today = LocalDate.now();

            Event event1 = new Event();
            event1.setTitle("AI & Machine Learning Hackathon 2026");
            event1.setDescription("A 24-hour national hackathon challenging students to build real-world AI and computer vision solutions. Mentors from top tech companies will be present.");
            event1.setDepartment("Computer Science and Engineering");
            event1.setDate(today.plusDays(7));
            event1.setTime(LocalTime.of(9, 30));
            event1.setVenue("Tech Hub Auditorium & Innovation Lab");
            event1.setType("Hackathon");
            event1.setCapacity(60);
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
            eventRepository.save(event5);
        }

        // Seed initial student reviews if empty
        if (feedbackRepository.count() == 0) {
            List<Event> events = eventRepository.findAll();
            if (!events.isEmpty()) {
                Event e1 = events.get(0);
                feedbackRepository.save(new Feedback(
                        e1.getId(),
                        "Aarav Sharma",
                        "aarav.sharma@campus.edu",
                        5,
                        "Incredible hackathon experience! Mentors provided great guidance on fine-tuning vision models, and the lab facilities were top-notch."
                ));
                feedbackRepository.save(new Feedback(
                        e1.getId(),
                        "Priya Venkatesh",
                        "priya.v@campus.edu",
                        4,
                        "Very well organized event. High computing power provided for model training. Looking forward to the next hackathon edition!"
                ));

                if (events.size() > 1) {
                    Event e2 = events.get(1);
                    feedbackRepository.save(new Feedback(
                            e2.getId(),
                            "Kavya Nair",
                            "kavya.nair@campus.edu",
                            5,
                            "Outstanding hands-on session. The Docker and Kubernetes deployment exercises were super clear and directly useful for our major projects."
                    ));
                    feedbackRepository.save(new Feedback(
                            e2.getId(),
                            "Rohan Deshmukh",
                            "rohan.d@campus.edu",
                            5,
                            "Best workshop attended this semester. Real-time CI/CD pipeline demonstrations on Google Cloud were fantastic."
                    ));
                }
            }
        }
    }
}
