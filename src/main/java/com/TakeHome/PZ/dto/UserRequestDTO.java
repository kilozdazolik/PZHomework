package com.TakeHome.PZ.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String name;
    private UUID familyId;
}

