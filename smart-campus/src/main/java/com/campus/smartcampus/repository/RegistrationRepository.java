package com.campus.smartcampus.repository;

import com.campus.smartcampus.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository
        extends JpaRepository<Registration, Long> {

    List<Registration> findByEventId(Long eventId);

    List<Registration> findByEventIdAndStatus(Long eventId, String status);

    List<Registration> findByEmailIgnoreCaseOrderByRegisteredAtDesc(String email);

    List<Registration> findByEmailIgnoreCase(String email);

    long countByEventId(Long eventId);

    long countByEventIdAndStatus(Long eventId, String status);

    long countByStatus(String status);

    boolean existsByEventIdAndEmailIgnoreCaseAndStatus(
            Long eventId,
            String email,
            String status
    );

    @Query("SELECT COALESCE(SUM(r.tickets), 0) FROM Registration r WHERE r.eventId = :eventId AND (r.status IS NULL OR r.status = 'CONFIRMED')")
    Long sumTicketsByEventId(@Param("eventId") Long eventId);

    @Query("SELECT COALESCE(SUM(r.tickets), 0) FROM Registration r WHERE (r.status IS NULL OR r.status = 'CONFIRMED')")
    Long sumTotalTickets();
}