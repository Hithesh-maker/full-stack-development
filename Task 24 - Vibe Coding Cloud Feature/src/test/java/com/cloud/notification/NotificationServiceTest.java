package com.cloud.notification;

import com.cloud.notification.dto.NotificationRequest;
import com.cloud.notification.dto.NotificationResponse;
import com.cloud.notification.model.NotificationEvent;
import com.cloud.notification.repository.NotificationRepository;
import com.cloud.notification.service.NotificationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Task 24 Cloud Notification Service Unit Tests")
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    @DisplayName("Should successfully dispatch notification event")
    void testDispatchNotification_Success() {
        NotificationRequest request = new NotificationRequest(
                "student@university.edu",
                "Fee Payment Confirmed",
                "Your tuition payment of $1200 has been recorded successfully.",
                "HIGH",
                "EMAIL"
        );

        NotificationEvent savedEvent = new NotificationEvent(
                request.getRecipient(),
                request.getSubject(),
                request.getMessageBody(),
                request.getPriority(),
                request.getChannel(),
                "SENT"
        );
        savedEvent.setId(101L);

        when(notificationRepository.save(any(NotificationEvent.class))).thenReturn(savedEvent);

        NotificationResponse response = notificationService.dispatchNotification(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(101L);
        assertThat(response.getRecipient()).isEqualTo("student@university.edu");
        assertThat(response.getDispatchStatus()).isEqualTo("SENT");
        verify(notificationRepository, times(1)).save(any(NotificationEvent.class));
    }

    @Test
    @DisplayName("Should retrieve notifications filtered by priority")
    void testGetAllNotifications_WithPriorityFilter() {
        NotificationEvent event = new NotificationEvent(
                "admin@campus.edu",
                "Server Alert",
                "CPU load exceeds 85%",
                "CRITICAL",
                "SMS",
                "SENT"
        );
        event.setId(5L);

        when(notificationRepository.findByPriority("CRITICAL")).thenReturn(Collections.singletonList(event));

        List<NotificationResponse> results = notificationService.getAllNotifications("CRITICAL");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getPriority()).isEqualTo("CRITICAL");
        verify(notificationRepository, times(1)).findByPriority("CRITICAL");
    }
}
