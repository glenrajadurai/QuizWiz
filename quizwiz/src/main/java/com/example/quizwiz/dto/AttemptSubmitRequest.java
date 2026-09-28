package com.example.quizwiz.dto;

import lombok.Data;
import java.util.List;

@Data
public class AttemptSubmitRequest {
    private List<AnswerSubmissionDTO> answers;
}
