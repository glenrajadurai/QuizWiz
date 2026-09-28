package com.example.quizwiz.dto;

import lombok.Data;

@Data
public class AnswerSubmissionDTO {
    private Long questionId;
    private String selectedOption;
}
