package com.campus.smartcampus.repository;

import com.campus.smartcampus.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByTeamCode(String teamCode);
    List<Team> findByEventId(Long eventId);
    List<Team> findByLeadEmailIgnoreCase(String leadEmail);
    long countByEventId(Long eventId);
}
