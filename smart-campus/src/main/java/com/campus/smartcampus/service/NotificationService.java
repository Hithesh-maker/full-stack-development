package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Event;
import com.campus.smartcampus.entity.NotificationLog;
import com.campus.smartcampus.entity.Registration;
import com.campus.smartcampus.repository.NotificationLogRepository;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationLogRepository notificationLogRepository;

    public NotificationService(NotificationLogRepository notificationLogRepository) {
        this.notificationLogRepository = notificationLogRepository;
    }

    public List<NotificationLog> getAllNotifications() {
        return notificationLogRepository.findAllByOrderBySentAtDesc();
    }

    public NotificationLog getNotificationById(Long id) {
        return notificationLogRepository.findById(id).orElse(null);
    }

    public List<NotificationLog> getNotificationsByEmail(String email) {
        if (email == null || email.isBlank()) return List.of();
        return notificationLogRepository.findByRecipientEmailIgnoreCaseOrderBySentAtDesc(email.trim());
    }

    public List<NotificationLog> getNotificationsByChannel(String channel) {
        if (channel == null || channel.isBlank() || "ALL".equalsIgnoreCase(channel)) {
            return getAllNotifications();
        }
        return notificationLogRepository.findByChannelOrderBySentAtDesc(channel.toUpperCase().trim());
    }

    public long getCountByChannel(String channel) {
        return notificationLogRepository.countByChannel(channel.toUpperCase().trim());
    }

    // 1. Booking Confirmation Email & Multi-Channel Dispatch
    public NotificationLog sendBookingConfirmation(Registration reg) {
        Event event = reg.getEvent();
        String eventTitle = event != null ? event.getTitle() : "Smart Campus Event";
        String eventDate = event != null ? event.getDate().toString() : "Upcoming";
        String eventVenue = event != null ? event.getVenue() : "Campus Hall";
        String subject = "Confirmed: Admission Pass for " + eventTitle + " [" + reg.getToken() + "]";

        String bodyHtml = buildEmailTemplate(
                "Booking Confirmed & Admission Pass Issued",
                "Dear " + reg.getStudentName() + ",",
                "Your registration for <strong>" + escape(eventTitle) + "</strong> has been confirmed. Below are your verified admission credentials for entry.",
                reg.getToken(),
                eventDate,
                eventVenue,
                reg.getTickets() + " Pass(es)",
                "Present your QR code pass at the venue entrance. Arrive 15 minutes before the scheduled start time.",
                "View My Admission Passes",
                "http://localhost:8080/my-registrations"
        );

        NotificationLog emailLog = new NotificationLog(reg.getEmail(), reg.getStudentName(), subject, "BOOKING_CONFIRMATION", bodyHtml, "EMAIL");
        emailLog = notificationLogRepository.save(emailLog);

        // Simulated WhatsApp dispatch
        try {
            String waContent = "Hello " + reg.getStudentName() + "! Your pass for " + eventTitle +
                    " is confirmed (Token: " + reg.getToken() + "). Gate access at " + eventVenue + ". View pass at: http://localhost:8080/my-registrations";
            NotificationLog waLog = new NotificationLog(
                    reg.getEmail(),
                    reg.getStudentName(),
                    "WhatsApp: " + eventTitle + " Pass Token " + reg.getToken(),
                    "WHATSAPP_PASS",
                    waContent,
                    "WHATSAPP"
            );
            notificationLogRepository.save(waLog);
        } catch (Exception ignored) {
        }

        // Simulated SMS dispatch
        try {
            String smsContent = "SmartCampus: Pass " + reg.getToken() + " confirmed for " + eventTitle + " on " + eventDate + ". Gate entry requires QR.";
            NotificationLog smsLog = new NotificationLog(
                    reg.getEmail(),
                    reg.getStudentName(),
                    "SMS: Pass " + reg.getToken() + " Confirmed",
                    "SMS_ALERT",
                    smsContent,
                    "SMS"
            );
            notificationLogRepository.save(smsLog);
        } catch (Exception ignored) {
        }

        return emailLog;
    }

    // 2. Waitlist Auto-Promotion Alert Email
    public NotificationLog sendWaitlistPromotionAlert(Registration reg) {
        Event event = reg.getEvent();
        String eventTitle = event != null ? event.getTitle() : "Campus Event";
        String eventDate = event != null ? event.getDate().toString() : "Upcoming";
        String eventVenue = event != null ? event.getVenue() : "Campus Hall";
        String subject = "🎉 Seat Secured: You've been promoted from Waitlist for " + eventTitle;

        String bodyHtml = buildEmailTemplate(
                "Good News! A Seat Has Opened Up For You",
                "Hello " + reg.getStudentName() + ",",
                "A previously booked attendee cancelled their ticket for <strong>" + escape(eventTitle) + "</strong>. As the next candidate on the waitlist, you have been automatically promoted to a <strong>CONFIRMED</strong> attendee!",
                reg.getToken(),
                eventDate,
                eventVenue,
                "1 Confirmed Seat",
                "Your admission pass has been automatically activated. Download your ticket or show your QR pass upon arrival.",
                "Download PDF Admission Pass",
                "http://localhost:8080/my-registrations"
        );

        NotificationLog log = new NotificationLog(reg.getEmail(), reg.getStudentName(), subject, "WAITLIST_PROMOTION", bodyHtml);
        return notificationLogRepository.save(log);
    }

    // 3. Checked-In Door Welcome Email
    public NotificationLog sendCheckInWelcome(Registration reg) {
        Event event = reg.getEvent();
        String eventTitle = event != null ? event.getTitle() : "Campus Event";
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("hh:mm a, MMM dd");
        String timeStr = reg.getCheckedInAt() != null ? reg.getCheckedInAt().format(dtf) : "Just now";
        String subject = "✓ Welcome: Checked-in to " + eventTitle;

        String bodyHtml = buildEmailTemplate(
                "Door Check-In Verified",
                "Welcome, " + reg.getStudentName() + "!",
                "Your admission pass for <strong>" + escape(eventTitle) + "</strong> was verified by the entrance scanner at <strong>" + timeStr + "</strong>. We hope you enjoy the session.",
                reg.getToken(),
                event != null ? event.getDate().toString() : "",
                event != null ? event.getVenue() : "",
                "Verified Entry",
                "Following the event, you will receive a link to provide feedback and download your verified Certificate of Participation.",
                "Submit Event Feedback & Rating",
                "http://localhost:8080/events/" + reg.getEventId() + "/feedback"
        );

        NotificationLog log = new NotificationLog(reg.getEmail(), reg.getStudentName(), subject, "CHECKIN_VERIFIED", bodyHtml);
        return notificationLogRepository.save(log);
    }

    // 4. E-Certificate Ready Email
    public NotificationLog sendCertificateReadyAlert(Registration reg) {
        Event event = reg.getEvent();
        String eventTitle = event != null ? event.getTitle() : "Campus Event";
        String subject = "🎓 Official Certificate of Participation Ready: " + eventTitle;

        String bodyHtml = buildEmailTemplate(
                "Official Certificate Issued",
                "Dear " + reg.getStudentName() + ",",
                "Congratulations on attending <strong>" + escape(eventTitle) + "</strong>! Your official University Certificate of Participation has been generated and digitally verified.",
                reg.getToken(),
                event != null ? event.getDate().toString() : "",
                event != null ? event.getVenue() : "",
                "Verified Graduate Pass",
                "Your certificate includes the University seal and cryptographic verification token for your resume and portfolio.",
                "Download E-Certificate (PDF)",
                "http://localhost:8080/registrations/" + reg.getId() + "/certificate"
        );

        NotificationLog log = new NotificationLog(reg.getEmail(), reg.getStudentName(), subject, "CERTIFICATE_READY", bodyHtml);
        return notificationLogRepository.save(log);
    }

    private String buildEmailTemplate(
            String headerTitle, String salutation, String mainText,
            String token, String date, String venue, String passes,
            String subNote, String buttonText, String buttonUrl) {

        return "<div style=\"font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; max-width: 600px; margin: 0 auto; background: #ffffff; border: 1px solid #e2e8f0; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05);\">"
                + "<div style=\"background: #0f172a; padding: 24px; text-align: center; color: #ffffff;\">"
                + "<div style=\"display: inline-block; background: #991b1b; color: #ffffff; font-weight: 800; font-size: 0.85rem; padding: 4px 10px; border-radius: 4px; letter-spacing: 0.5px; margin-bottom: 8px;\">EDU</div>"
                + "<h2 style=\"margin: 0; font-size: 1.35rem; color: #f8fafc;\">Smart Campus University</h2>"
                + "<p style=\"margin: 4px 0 0 0; font-size: 0.82rem; color: #94a3b8;\">Official Event Management & Ticketing Office</p>"
                + "</div>"
                + "<div style=\"padding: 28px 24px;\">"
                + "<div style=\"background: #ecfdf5; border-left: 4px solid #10b981; padding: 12px 16px; border-radius: 4px; margin-bottom: 20px;\">"
                + "<strong style=\"color: #065f46; font-size: 1.05rem;\">" + headerTitle + "</strong>"
                + "</div>"
                + "<p style=\"font-size: 0.95rem; color: #334155; margin-bottom: 12px;\">" + salutation + "</p>"
                + "<p style=\"font-size: 0.92rem; color: #475569; line-height: 1.6; margin-bottom: 20px;\">" + mainText + "</p>"
                + "<div style=\"background: #f8fafc; border: 1.5px solid #e2e8f0; border-radius: 8px; padding: 16px; margin-bottom: 24px;\">"
                + "<div style=\"font-size: 0.78rem; text-transform: uppercase; color: #64748b; font-weight: 700; margin-bottom: 6px;\">Admission Verification Token</div>"
                + "<div style=\"font-family: monospace; font-size: 1.25rem; font-weight: 800; color: #0f172a; letter-spacing: 1px;\">" + token + "</div>"
                + "<div style=\"margin-top: 12px; font-size: 0.84rem; color: #334155; display: flex; flex-direction: column; gap: 4px;\">"
                + "<div>📅 <strong>Date:</strong> " + date + "</div>"
                + "<div>📍 <strong>Venue:</strong> " + venue + "</div>"
                + "<div>🎟️ <strong>Admission:</strong> " + passes + "</div>"
                + "</div>"
                + "</div>"
                + "<p style=\"font-size: 0.86rem; color: #64748b; line-height: 1.5; margin-bottom: 24px;\">" + subNote + "</p>"
                + "<div style=\"text-align: center; margin-bottom: 12px;\">"
                + "<a href=\"" + buttonUrl + "\" style=\"display: inline-block; background: #991b1b; color: #ffffff; text-decoration: none; padding: 12px 28px; border-radius: 6px; font-weight: 700; font-size: 0.95rem;\">" + buttonText + " →</a>"
                + "</div>"
                + "</div>"
                + "<div style=\"background: #f1f5f9; padding: 16px 24px; text-align: center; font-size: 0.76rem; color: #64748b; border-top: 1px solid #e2e8f0;\">"
                + "Smart Campus University Center, Innovation Block, Bengaluru, Karnataka, India<br>"
                + "This is an automated system notification from the Smart Campus Portal."
                + "</div>"
                + "</div>";
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
