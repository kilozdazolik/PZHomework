package com.TakeHome.PZ.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ApplicationRequestDTO {
    @NotBlank(message = "Application name cannot be empty!")
    @Size(min = 2, max = 50, message = "Application name must be between 2 and 50 characters!")
    private String name;

    @NotNull(message = "User ID is required!")
    private UUID userId;
}