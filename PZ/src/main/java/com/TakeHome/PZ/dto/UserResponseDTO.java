package com.TakeHome.PZ.dto;
import lombok.Data;
import java.util.UUID;

@Data
public class UserResponseDTO {
    private UUID id;
    private String name;
    private String role;
    private String familyName;
}