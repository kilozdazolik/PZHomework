package com.TakeHome.PZ.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiCommandRequestDTO {
    @NotBlank(message = "userId is required")
    private String userId;

    @NotBlank(message = "prompt is required")
    private String prompt;
}
