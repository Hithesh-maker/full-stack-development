package com.campus.smartcampus.repository;

import com.campus.smartcampus.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findAllByOrderBySentAtDesc();
    List<NotificationLog> findByRecipientEmailIgnoreCaseOrderBySentAtDesc(String email);
    List<NotificationLog> findByEventTypeOrderBySentAtDesc(String eventType);
    List<NotificationLog> findByChannelOrderBySentAtDesc(String channel);
    long countByChannel(String channel);
}
