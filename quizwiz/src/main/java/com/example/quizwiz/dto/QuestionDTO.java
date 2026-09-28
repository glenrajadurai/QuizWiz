package com.example.quizwiz.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class QuestionDTO {
    @NotBlank(message = "Question text is required")
    private String questionText;
    @NotBlank private String optionA;
    @NotBlank private String optionB;
    @NotBlank private String optionC;
    @NotBlank private String optionD;
    @NotBlank @Pattern(regexp = "^[A-D]$", message = "Correct option must be A, B, C, or D")
    private String correctOption;
}
