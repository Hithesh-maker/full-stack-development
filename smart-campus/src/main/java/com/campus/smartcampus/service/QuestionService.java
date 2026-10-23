package com.campus.smartcampus.service;

import com.campus.smartcampus.entity.Question;
import com.campus.smartcampus.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public List<Question> getQuestionsForEvent(Long eventId) {
        if (eventId == null) return List.of();
        return questionRepository.findByEventIdOrderByUpvotesDescCreatedAtDesc(eventId);
    }

    public Question askQuestion(Long eventId, String studentName, String studentEmail, String questionText) {
        if (eventId == null) throw new RuntimeException("Event ID is required");
        if (studentName == null || studentName.trim().isBlank()) throw new RuntimeException("Student name is required");
        if (questionText == null || questionText.trim().isBlank()) throw new RuntimeException("Question text cannot be blank");

        Question question = new Question(eventId, studentName.trim(), studentEmail != null ? studentEmail.trim() : "", questionText.trim());
        return questionRepository.save(question);
    }

    public Question upvoteQuestion(Long questionId) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + questionId));
        q.setUpvotes(q.getUpvotes() + 1);
        return questionRepository.save(q);
    }

    public Question toggleAnswered(Long questionId) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + questionId));
        q.setIsAnswered(!Boolean.TRUE.equals(q.getIsAnswered()));
        return questionRepository.save(q);
    }

    public void deleteQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
    }

    public long getUnansweredCount(Long eventId) {
        return questionRepository.countByEventId(eventId) - questionRepository.countByEventIdAndIsAnsweredTrue(eventId);
    }
}
