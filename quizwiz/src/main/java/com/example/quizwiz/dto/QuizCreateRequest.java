package com.example.quizwiz.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class QuizCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;
    private String description;
    @NotNull @Positive(message = "Time limit must be positive")
    private Integer timeLimitInMinutes;
    @NotEmpty(message = "At least one question is required")
    private List<QuestionDTO> questions;
}
