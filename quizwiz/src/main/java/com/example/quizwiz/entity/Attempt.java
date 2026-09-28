package com.example.quizwiz.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "attempts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Attempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    private LocalDateTime startTime;
    private LocalDateTime submissionTime;

    private Double score;

    @Enumerated(EnumType.STRING)
    private AttemptStatus status;

    public enum AttemptStatus {
        IN_PROGRESS, SUBMITTED, EXPIRED
    }
}
