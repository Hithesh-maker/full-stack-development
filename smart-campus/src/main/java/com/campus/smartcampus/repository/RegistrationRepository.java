package com.campus.smartcampus.repository;

import com.campus.smartcampus.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistrationRepository
        extends JpaRepository<Registration, Long> {

    List<Registration> findByEventId(Long eventId);

    List<Registration> findByEmailIgnoreCase(String email);

    long countByEventId(Long eventId);

    long countByEventIdAndDepartmentIgnoreCase(
            Long eventId,
            String department
    );
}