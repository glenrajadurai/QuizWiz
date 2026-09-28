package com.example.quizwiz.controller;

import com.example.quizwiz.dto.*;
import com.example.quizwiz.entity.*;
import com.example.quizwiz.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/quizzes")
    public ResponseEntity<Quiz> createQuiz(@Valid @RequestBody QuizCreateRequest request) {
        Quiz createdQuiz = quizService.createQuiz(request);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

    @PostMapping("/quizzes/{quizId}/start")
    public ResponseEntity<Attempt> startAttempt(@PathVariable Long quizId, @RequestParam Long studentId) {
        Attempt attempt = quizService.startAttempt(quizId, studentId);
        return new ResponseEntity<>(attempt, HttpStatus.CREATED);
    }

    @PostMapping("/attempts/{attemptId}/submit")
    public ResponseEntity<Attempt> submitAttempt(@PathVariable Long attemptId, @RequestBody AttemptSubmitRequest request) {
        Attempt attempt = quizService.submitAttempt(attemptId, request);
        return ResponseEntity.ok(attempt);
    }

    @GetMapping("/quizzes/{quizId}/reports")
    public ResponseEntity<List<Attempt>> getQuizReport(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizReport(quizId));
    }
}
