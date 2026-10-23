package com.campus.smartcampus.controller;

import com.campus.smartcampus.entity.NotificationLog;
import com.campus.smartcampus.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class AdminNotificationController {

    private final NotificationService notificationService;

    public AdminNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/admin/notifications")
    public String notificationsPage(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String channel,
            Model model) {
        String activeChannel = (channel != null && !channel.isBlank()) ? channel.toUpperCase() : "ALL";
        model.addAttribute("notifications", notificationService.getNotificationsByChannel(activeChannel));
        model.addAttribute("activeChannel", activeChannel);
        model.addAttribute("totalCount", notificationService.getAllNotifications().size());
        model.addAttribute("emailCount", notificationService.getCountByChannel("EMAIL"));
        model.addAttribute("whatsappCount", notificationService.getCountByChannel("WHATSAPP"));
        model.addAttribute("smsCount", notificationService.getCountByChannel("SMS"));
        return "admin-notifications";
    }

    @GetMapping("/api/admin/notifications/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getNotificationDetails(@PathVariable Long id) {
        NotificationLog notif = notificationService.getNotificationById(id);
        if (notif == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of(
                "id", notif.getId(),
                "recipientEmail", notif.getRecipientEmail(),
                "studentName", notif.getStudentName(),
                "subject", notif.getSubject(),
                "eventType", notif.getEventType(),
                "channel", notif.getChannel(),
                "bodyHtml", notif.getBodyHtml() != null ? notif.getBodyHtml() : "",
                "sentAt", notif.getSentAt() != null ? notif.getSentAt().toString() : "",
                "status", notif.getStatus()
        ));
    }
}
