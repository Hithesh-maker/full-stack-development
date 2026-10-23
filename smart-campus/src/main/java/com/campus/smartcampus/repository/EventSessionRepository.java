package com.campus.smartcampus.repository;

import com.campus.smartcampus.entity.EventSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventSessionRepository extends JpaRepository<EventSession, Long> {
    List<EventSession> findByEventIdOrderBySessionOrderAscStartTimeAsc(Long eventId);
    long countByEventId(Long eventId);
}
