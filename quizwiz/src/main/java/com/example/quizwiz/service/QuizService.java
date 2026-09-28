package com.example.quizwiz.service;

import com.example.quizwiz.dto.*;
import com.example.quizwiz.entity.*;
import com.example.quizwiz.exception.*;
import com.example.quizwiz.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final StudentRepository studentRepository;
    private final AttemptRepository attemptRepository;

    public QuizService(QuizRepository quizRepository, StudentRepository studentRepository, AttemptRepository attemptRepository) {
        this.quizRepository = quizRepository;
        this.studentRepository = studentRepository;
        this.attemptRepository = attemptRepository;
    }

    @Transactional
    public Quiz createQuiz(QuizCreateRequest request) {
        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setDescription(request.getDescription());
        quiz.setTimeLimitInMinutes(request.getTimeLimitInMinutes());

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
        return quizRepository.save(quiz);
    }

    @Transactional
    public Attempt startAttempt(Long quizId, Long studentId) {
        if (attemptRepository.existsByStudentIdAndQuizId(studentId, quizId)) {
            throw new QuizException("Student has already attempted this quiz.");
        }

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + quizId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

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
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with ID: " + attemptId));

        if (attempt.getStatus() != Attempt.AttemptStatus.IN_PROGRESS) {
            throw new QuizException("Attempt is already submitted or expired.");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime deadline = attempt.getStartTime().plusMinutes(attempt.getQuiz().getTimeLimitInMinutes());

        boolean isExpired = now.isAfter(deadline);
        attempt.setSubmissionTime(now);

        Quiz quiz = attempt.getQuiz();
        Map<Long, String> correctAnswersMap = new HashMap<>();
        for (Question q : quiz.getQuestions()) {
            correctAnswersMap.put(q.getId(), q.getCorrectOption());
        }

        double correctCount = 0;
        if (request.getAnswers() != null) {
            for (AnswerSubmissionDTO sub : request.getAnswers()) {
                String correct = correctAnswersMap.get(sub.getQuestionId());
                if (correct != null && correct.equalsIgnoreCase(sub.getSelectedOption())) {
                    correctCount++;
                }
            }
        }

        double finalScore = (correctCount / quiz.getQuestions().size()) * 100.0;
        attempt.setScore(finalScore);
        attempt.setStatus(isExpired ? Attempt.AttemptStatus.EXPIRED : Attempt.AttemptStatus.SUBMITTED);

        return attemptRepository.save(attempt);
    }

    public List<Attempt> getQuizReport(Long quizId) {
        return attemptRepository.findByQuizId(quizId);
    }
}
