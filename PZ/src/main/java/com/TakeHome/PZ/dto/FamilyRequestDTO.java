package com.TakeHome.PZ.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
public class FamilyRequestDTO {
    @NotBlank(message = "Name cannot be empty!")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters!")
    @Pattern(regexp = "^[a-zA-ZáéíóöőúüűÁÉÍÓÖŐÚÜŰ ]+$", message = "Name cannot contain numbers or special characters!")
    private String name;
}