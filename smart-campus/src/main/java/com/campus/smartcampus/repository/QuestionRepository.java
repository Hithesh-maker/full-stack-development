package com.campus.smartcampus.repository;

import com.campus.smartcampus.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByEventIdOrderByUpvotesDescCreatedAtDesc(Long eventId);
    long countByEventId(Long eventId);
    long countByEventIdAndIsAnsweredTrue(Long eventId);
}
