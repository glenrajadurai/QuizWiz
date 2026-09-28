package com.example.quizwiz.controller;

import com.example.quizwiz.dto.*;
import com.example.quizwiz.entity.*;
import com.example.quizwiz.repository.StudentRepository;
import com.example.quizwiz.repository.UserRepository;
import com.example.quizwiz.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class QuizController {

    private final QuizService quizService;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public QuizController(QuizService quizService, UserRepository userRepository, StudentRepository studentRepository) {
        this.quizService = quizService;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    // USER REGISTRATION
    @PostMapping("/users/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        User savedUser = userRepository.save(user);

        // Auto-create matching Student record if role is STUDENT
        if (savedUser.getRole() == User.Role.STUDENT) {
            Student student = new Student();
            student.setName(savedUser.getUsername());
            student.setEmail(savedUser.getUsername().toLowerCase() + "@quizwiz.com");
            studentRepository.save(student);
        }

        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // USER LOGIN WITH PASSWORD CHECK
    @PostMapping("/users/login")
    public ResponseEntity<User> loginUser(@RequestBody User loginRequest) {
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok(user);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // QUIZ ENDPOINTS
    @GetMapping("/quizzes")
    public ResponseEntity<List<Quiz>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    @PostMapping("/quizzes")
    public ResponseEntity<Quiz> createQuiz(@Valid @RequestBody QuizCreateRequest request) {
        Quiz createdQuiz = quizService.createQuiz(request);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

    @PostMapping("/quizzes/{quizId}/start")
    public ResponseEntity<Attempt> startAttempt(@PathVariable Long quizId, @RequestParam Long studentId) {
        // If studentId doesn't match directly, resolve from the first available Student record or create on-the-fly
        Long resolvedStudentId = studentId;
        if (!studentRepository.existsById(studentId)) {
            List<Student> students = studentRepository.findAll();
            if (!students.isEmpty()) {
                resolvedStudentId = students.get(0).getId();
            } else {
                Student fallbackStudent = new Student();
                fallbackStudent.setName("Default Student");
                fallbackStudent.setEmail("student@quizwiz.com");
                fallbackStudent = studentRepository.save(fallbackStudent);
                resolvedStudentId = fallbackStudent.getId();
            }
        }

        Attempt attempt = quizService.startAttempt(quizId, resolvedStudentId);
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