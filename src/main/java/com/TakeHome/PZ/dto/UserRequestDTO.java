package com.TakeHome.PZ.dto;

import java.util.UUID;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


@Data
public class UserRequestDTO {
    @NotBlank(message = "Name cannot be empty!")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters!")
    private String name;

    @NotNull(message = "Family ID is required!")
    private UUID familyId;
}
