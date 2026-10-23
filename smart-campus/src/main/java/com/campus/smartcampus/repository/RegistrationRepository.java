package com.campus.smartcampus.repository;

import com.campus.smartcampus.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository
        extends JpaRepository<Registration, Long> {

    List<Registration> findByEventId(Long eventId);

    List<Registration> findByEventIdAndStatus(Long eventId, String status);

    List<Registration> findByEventIdAndStatusOrderByIdAsc(Long eventId, String status);

    Optional<Registration> findFirstByEventIdAndStatusOrderByIdAsc(Long eventId, String status);

    List<Registration> findByStatusOrderByCheckedInAtDesc(String status);

    List<Registration> findByEventIdAndStatusOrderByCheckedInAtDesc(Long eventId, String status);

    Optional<Registration> findByToken(String token);

    boolean existsByToken(String token);

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

    @Query("SELECT COUNT(r) > 0 FROM Registration r WHERE r.eventId = :eventId AND LOWER(r.email) = LOWER(:email) AND r.status IN ('CONFIRMED', 'CHECKED_IN', 'WAITLISTED')")
    boolean existsActiveRegistration(@Param("eventId") Long eventId, @Param("email") String email);

    @Query("SELECT COALESCE(SUM(r.tickets), 0) FROM Registration r WHERE r.eventId = :eventId AND (r.status IS NULL OR r.status = 'CONFIRMED' OR r.status = 'CHECKED_IN')")
    Long sumTicketsByEventId(@Param("eventId") Long eventId);

    @Query("SELECT COALESCE(SUM(r.tickets), 0) FROM Registration r WHERE (r.status IS NULL OR r.status = 'CONFIRMED' OR r.status = 'CHECKED_IN')")
    Long sumTotalTickets();

    @Query("SELECT r.eventId, COALESCE(SUM(r.tickets), 0) FROM Registration r WHERE (r.status IS NULL OR r.status = 'CONFIRMED' OR r.status = 'CHECKED_IN') GROUP BY r.eventId")
    List<Object[]> sumTicketsGroupedByEventId();
}