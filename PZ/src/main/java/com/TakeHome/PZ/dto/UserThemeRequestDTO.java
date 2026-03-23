package com.TakeHome.PZ.dto;

import com.TakeHome.PZ.models.Enums.Theme;

import lombok.Data;

@Data
public class UserThemeRequestDTO {
    private Theme theme;
}