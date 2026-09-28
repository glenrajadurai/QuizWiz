package com.example.quizwiz.service;

import com.example.quizwiz.dto.*;
import com.example.quizwiz.entity.*;
import com.example.quizwiz.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final StudentRepository studentRepository;
    private final AttemptRepository attemptRepository;

    public QuizService(QuizRepository quizRepository,
                       StudentRepository studentRepository,
                       AttemptRepository attemptRepository) {
        this.quizRepository = quizRepository;
        this.studentRepository = studentRepository;
        this.attemptRepository = attemptRepository;
    }

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }

    @Transactional
    public Quiz createQuiz(QuizCreateRequest request) {
        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setTimeLimitInMinutes(request.getTimeLimitInMinutes());

        if (request.getQuestions() != null) {
            List<Question> questions = request.getQuestions().stream().map(qDto -> {
                Question q = new Question();
                q.setQuestionText(qDto.getQuestionText());
                q.setOptionA(qDto.getOptionA());
                q.setOptionB(qDto.getOptionB());
                q.setOptionC(qDto.getOptionC());
                q.setOptionD(qDto.getOptionD());
                q.setCorrectOption(qDto.getCorrectOption());
                q.setQuiz(quiz);
                return q;
            }).toList();
            quiz.setQuestions(questions);
        }

        return quizRepository.save(quiz);
    }

    @Transactional
    public Attempt startAttempt(Long quizId, Long studentId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found with ID: " + quizId));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student record not found for ID: " + studentId));

        Attempt attempt = new Attempt();
        attempt.setQuiz(quiz);
        attempt.setStudent(student);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setStatus(Attempt.AttemptStatus.IN_PROGRESS);

        return attemptRepository.save(attempt);
    }

    @Transactional
    public Attempt submitAttempt(Long attemptId, AttemptSubmitRequest request) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new RuntimeException("Attempt not found with ID: " + attemptId));

        attempt.setSubmissionTime(LocalDateTime.now());
        attempt.setStatus(Attempt.AttemptStatus.SUBMITTED);

        // Simple score calculation placeholder (can adapt to your exact calculation logic)
        attempt.setScore(100.0);

        return attemptRepository.save(attempt);
    }

    public List<Attempt> getQuizReport(Long quizId) {
        return attemptRepository.findByQuizId(quizId);
    }
}