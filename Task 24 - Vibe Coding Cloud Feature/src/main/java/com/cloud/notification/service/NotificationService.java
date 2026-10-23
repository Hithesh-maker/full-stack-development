package com.cloud.notification.service;

import com.cloud.notification.dto.NotificationRequest;
import com.cloud.notification.dto.NotificationResponse;
import com.cloud.notification.model.NotificationEvent;
import com.cloud.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public NotificationResponse dispatchNotification(NotificationRequest request) {
        logger.info("[Notification Dispatcher] Ingesting event for recipient: {} via {}",
                request.getRecipient(), request.getChannel());

        // In a real cloud environment, this would publish to AWS SNS/SQS, GCP Pub/Sub, or Azure Service Bus
        String dispatchStatus = "SENT";
        if ("CRITICAL".equalsIgnoreCase(request.getPriority())) {
            logger.warn("[URGENT ALERT] High priority notification dispatched immediately to: {}", request.getRecipient());
        }

        NotificationEvent event = new NotificationEvent(
                request.getRecipient(),
                request.getSubject(),
                request.getMessageBody(),
                request.getPriority().toUpperCase(),
                request.getChannel().toUpperCase(),
                dispatchStatus
        );

        NotificationEvent saved = notificationRepository.save(event);

        return new NotificationResponse(
                saved.getId(),
                saved.getRecipient(),
                saved.getSubject(),
                saved.getPriority(),
                saved.getChannel(),
                saved.getDispatchStatus(),
                saved.getCreatedAt(),
                "Notification processed and dispatched successfully."
        );
    }

    public List<NotificationResponse> getAllNotifications(String priority) {
        List<NotificationEvent> events;
        if (priority != null && !priority.isBlank()) {
            events = notificationRepository.findByPriority(priority.toUpperCase());
        } else {
            events = notificationRepository.findAll();
        }

        return events.stream()
                .map(e -> new NotificationResponse(
                        e.getId(),
                        e.getRecipient(),
                        e.getSubject(),
                        e.getPriority(),
                        e.getChannel(),
                        e.getDispatchStatus(),
                        e.getCreatedAt(),
                        "Dispatched via " + e.getChannel()
                ))
                .collect(Collectors.toList());
    }

    public NotificationResponse getNotificationById(Long id) {
        NotificationEvent event = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + id));

        return new NotificationResponse(
                event.getId(),
                event.getRecipient(),
                event.getSubject(),
                event.getPriority(),
                event.getChannel(),
                event.getDispatchStatus(),
                event.getCreatedAt(),
                "Retrieved successfully"
        );
    }
}
