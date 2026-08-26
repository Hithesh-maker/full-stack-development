package com.campus.smartcampus.repository;

import com.campus.smartcampus.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Find upcoming events
    List<Event> findByDateGreaterThanEqual(LocalDate date);

    // Filter by department
    List<Event> findByDepartmentIgnoreCase(String department);

    // Filter by event type
    List<Event> findByTypeIgnoreCase(String type);

    // Filter by department and event type
    List<Event> findByDepartmentIgnoreCaseAndTypeIgnoreCase(
            String department,
            String type
    );
}