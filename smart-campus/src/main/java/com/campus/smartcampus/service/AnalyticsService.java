package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.Feedback;
import com.campus.smartcampus.entity.Registration;
import com.campus.smartcampus.repository.EventRepository;
import com.campus.smartcampus.repository.FeedbackRepository;
import com.campus.smartcampus.repository.RegistrationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;
    private final FeedbackRepository feedbackRepository;

    public AnalyticsService(EventRepository eventRepository,
                            RegistrationRepository registrationRepository,
                            FeedbackRepository feedbackRepository) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.feedbackRepository = feedbackRepository;
    }

    public Map<String, Object> getDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        List<Event> allEvents = eventRepository.findAll();
        List<Registration> allRegistrations = registrationRepository.findAll();

        long totalEvents = allEvents.size();
        long upcomingEvents = allEvents.stream().filter(e -> !e.getDate().isBefore(LocalDate.now())).count();
        long pastEvents = totalEvents - upcomingEvents;

        long totalTickets = 0;
        long confirmedCount = 0;
        long checkedInCount = 0;
        long waitlistCount = 0;
        long cancelledCount = 0;
        double totalRevenue = 0.0;

        Map<String, Long> departmentRegistrations = new LinkedHashMap<>();

        for (Registration r : allRegistrations) {
            String status = r.getStatus() != null ? r.getStatus().toUpperCase() : "CONFIRMED";
            switch (status) {
                case "CONFIRMED" -> {
                    confirmedCount++;
                    totalTickets += (r.getTickets() != null ? r.getTickets() : 1);
                }
                case "CHECKED_IN" -> {
                    checkedInCount++;
                    totalTickets += (r.getTickets() != null ? r.getTickets() : 1);
                }
                case "WAITLISTED" -> waitlistCount++;
                case "CANCELLED" -> cancelledCount++;
            }

            if (r.getPaymentAmount() != null && r.getPaymentAmount() > 0) {
                totalRevenue += r.getPaymentAmount();
            }

            String dept = r.getDepartment() != null && !r.getDepartment().isBlank() ? r.getDepartment() : "General";
            departmentRegistrations.put(dept, departmentRegistrations.getOrDefault(dept, 0L) + 1);
        }

        long activeRegistrations = confirmedCount + checkedInCount;
        double turnoutPercentage = activeRegistrations > 0 ? ((double) checkedInCount / activeRegistrations) * 100.0 : 0.0;

        metrics.put("totalEvents", totalEvents);
        metrics.put("upcomingEvents", upcomingEvents);
        metrics.put("pastEvents", pastEvents);
        metrics.put("totalRegistrations", allRegistrations.size());
        metrics.put("activeRegistrations", activeRegistrations);
        metrics.put("totalTickets", totalTickets);
        metrics.put("confirmedCount", confirmedCount);
        metrics.put("checkedInCount", checkedInCount);
        metrics.put("waitlistCount", waitlistCount);
        metrics.put("cancelledCount", cancelledCount);
        metrics.put("turnoutPercentage", Math.round(turnoutPercentage * 10.0) / 10.0);
        metrics.put("totalRevenue", totalRevenue);
        metrics.put("departmentRegistrations", departmentRegistrations);

        // Per-event utilization summary
        List<Map<String, Object>> eventUtilization = allEvents.stream().map(e -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", e.getId());
            map.put("title", e.getTitle());
            map.put("date", e.getDate());
            map.put("capacity", e.getCapacity());
            long booked = registrationRepository.countByEventIdAndStatus(e.getId(), "CONFIRMED")
                    + registrationRepository.countByEventIdAndStatus(e.getId(), "CHECKED_IN");
            long checkedIn = registrationRepository.countByEventIdAndStatus(e.getId(), "CHECKED_IN");
            double util = e.getCapacity() > 0 ? ((double) booked / e.getCapacity()) * 100.0 : 0.0;
            map.put("booked", booked);
            map.put("checkedIn", checkedIn);
            map.put("utilization", Math.round(Math.min(util, 100.0) * 10.0) / 10.0);
            return map;
        }).collect(Collectors.toList());

        metrics.put("eventUtilization", eventUtilization);

        // Event Type / Category distribution
        Map<String, Long> eventTypeDistribution = new LinkedHashMap<>();
        for (Event e : allEvents) {
            String type = e.getType() != null && !e.getType().isBlank() ? e.getType() : "General";
            eventTypeDistribution.put(type, eventTypeDistribution.getOrDefault(type, 0L) + 1);
        }
        metrics.put("eventTypeDistribution", eventTypeDistribution);

        // Registration Velocity (grouped by day)
        Map<String, Long> registrationVelocity = new LinkedHashMap<>();
        allRegistrations.stream()
                .filter(r -> r.getRegisteredAt() != null)
                .sorted(Comparator.comparing(Registration::getRegisteredAt))
                .forEach(r -> {
                    String dateKey = r.getRegisteredAt().format(DateTimeFormatter.ofPattern("MMM dd"));
                    registrationVelocity.put(dateKey, registrationVelocity.getOrDefault(dateKey, 0L) + 1);
                });
        if (registrationVelocity.isEmpty()) {
            registrationVelocity.put(LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd")), 0L);
        }
        metrics.put("registrationVelocity", registrationVelocity);

        // Feedback Sentiment & Keyword Analytics
        List<Feedback> allFeedbacks = feedbackRepository != null ? feedbackRepository.findAll() : List.of();
        long positiveCount = 0;
        long neutralCount = 0;
        long criticalCount = 0;
        double sumRating = 0.0;

        for (Feedback f : allFeedbacks) {
            int rating = f.getRating() != null ? f.getRating() : 5;
            sumRating += rating;
            if (rating >= 4) {
                positiveCount++;
            } else if (rating == 3) {
                neutralCount++;
            } else {
                criticalCount++;
            }
        }

        double avgRating = !allFeedbacks.isEmpty() ? Math.round((sumRating / allFeedbacks.size()) * 10.0) / 10.0 : 0.0;
        Map<String, Integer> topKeywords = extractTopKeywords(allFeedbacks);

        metrics.put("positiveFeedbackCount", positiveCount);
        metrics.put("neutralFeedbackCount", neutralCount);
        metrics.put("criticalFeedbackCount", criticalCount);
        metrics.put("totalFeedbackCount", (long) allFeedbacks.size());
        metrics.put("averageFeedbackRating", avgRating);
        metrics.put("topKeywords", topKeywords);

        return metrics;
    }

    public Map<String, Integer> extractTopKeywords(List<Feedback> feedbacks) {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return Collections.emptyMap();
        }

        Set<String> stopWords = Set.of(
            "the", "and", "a", "an", "in", "to", "of", "for", "with", "on", "at", "from",
            "by", "about", "as", "into", "like", "through", "after", "over", "between", "out",
            "against", "during", "without", "before", "under", "around", "among", "is", "was",
            "are", "were", "be", "been", "being", "have", "has", "had", "do", "does", "did",
            "this", "that", "these", "those", "i", "we", "you", "they", "it", "he", "she",
            "my", "our", "your", "their", "its", "event", "session", "very", "good", "great", "really",
            "campus", "college", "more", "also", "some", "well", "much"
        );

        Map<String, Integer> freq = new HashMap<>();
        for (Feedback f : feedbacks) {
            if (f.getComments() == null || f.getComments().isBlank()) continue;
            String cleaned = f.getComments().toLowerCase().replaceAll("[^a-z0-9\\s-]", " ");
            String[] tokens = cleaned.split("\\s+");
            for (String t : tokens) {
                t = t.trim();
                if (t.length() >= 4 && !stopWords.contains(t)) {
                    freq.put(t, freq.getOrDefault(t, 0) + 1);
                }
            }
        }

        return freq.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(8)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
