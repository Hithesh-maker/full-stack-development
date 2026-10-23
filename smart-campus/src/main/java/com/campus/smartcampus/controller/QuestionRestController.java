package com.campus.smartcampus.controller;

import com.campus.smartcampus.entity.Question;
import com.campus.smartcampus.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class QuestionRestController {

    private final QuestionService questionService;

    public QuestionRestController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/{eventId}/questions")
    public List<Question> getQuestions(@PathVariable Long eventId) {
        return questionService.getQuestionsForEvent(eventId);
    }

    @PostMapping("/{eventId}/questions")
    public ResponseEntity<Question> submitQuestion(
            @PathVariable Long eventId,
            @RequestBody Map<String, String> payload) {

        String studentName = payload.getOrDefault("studentName", "Anonymous Student");
        if (studentName.isBlank()) studentName = "Anonymous Student";
        String studentEmail = payload.getOrDefault("studentEmail", payload.getOrDefault("department", ""));
        String questionText = payload.getOrDefault("questionText", payload.getOrDefault("content", ""));

        Question q = questionService.askQuestion(eventId, studentName, studentEmail, questionText);
        return ResponseEntity.ok(q);
    }

    @PostMapping({"/questions/{id}/upvote", "/{eventId}/questions/{id}/upvote"})
    public ResponseEntity<Question> upvoteQuestion(@PathVariable Long id) {
        Question q = questionService.upvoteQuestion(id);
        return ResponseEntity.ok(q);
    }

    @PostMapping({"/questions/{id}/toggle-answered", "/{eventId}/questions/{id}/toggle-answered"})
    public ResponseEntity<Question> toggleAnswered(@PathVariable Long id) {
        Question q = questionService.toggleAnswered(id);
        return ResponseEntity.ok(q);
    }
}
