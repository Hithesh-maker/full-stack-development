package com.cloud.notification.repository;

import com.cloud.notification.model.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEvent, Long> {

    List<NotificationEvent> findByPriority(String priority);

    List<NotificationEvent> findByChannel(String channel);
}
