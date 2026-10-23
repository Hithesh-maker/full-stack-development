package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Question;
import com.campus.smartcampus.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionService questionService;

    private Question q1;

    @BeforeEach
    void setUp() {
        q1 = new Question();
        q1.setId(100L);
        q1.setEventId(1L);
        q1.setStudentName("Kavya");
        q1.setQuestionText("What are the prerequisites for this AI workshop?");
        q1.setUpvotes(2);
        q1.setIsAnswered(false);
    }

    @Test
    void testGetQuestionsForEvent() {
        when(questionRepository.findByEventIdOrderByUpvotesDescCreatedAtDesc(1L)).thenReturn(List.of(q1));

        List<Question> result = questionService.getQuestionsForEvent(1L);

        assertEquals(1, result.size());
        assertEquals("Kavya", result.get(0).getStudentName());
        assertEquals(2, result.get(0).getUpvotes());
    }

    @Test
    void testAskQuestion() {
        when(questionRepository.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

        Question saved = questionService.askQuestion(1L, "Rahul", "rahul@campus.edu", "Will slides be shared?");

        assertNotNull(saved);
        assertEquals(1L, saved.getEventId());
        assertEquals("Rahul", saved.getStudentName());
        assertEquals("Will slides be shared?", saved.getQuestionText());
        assertEquals(0, saved.getUpvotes());
        assertFalse(saved.getIsAnswered());
    }

    @Test
    void testUpvoteQuestion() {
        when(questionRepository.findById(100L)).thenReturn(Optional.of(q1));
        when(questionRepository.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

        Question updated = questionService.upvoteQuestion(100L);

        assertNotNull(updated);
        assertEquals(3, updated.getUpvotes());
    }

    @Test
    void testToggleAnswered() {
        when(questionRepository.findById(100L)).thenReturn(Optional.of(q1));
        when(questionRepository.save(any(Question.class))).thenAnswer(inv -> inv.getArgument(0));

        Question updated = questionService.toggleAnswered(100L);

        assertNotNull(updated);
        assertTrue(updated.getIsAnswered());
    }
}
