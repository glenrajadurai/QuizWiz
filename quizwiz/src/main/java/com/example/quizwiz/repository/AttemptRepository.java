package com.example.quizwiz.repository;

import com.example.quizwiz.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {
    boolean existsByStudentIdAndQuizId(Long studentId, Long quizId);
    List<Attempt> findByQuizId(Long quizId);
}
