package com.TakeHome.PZ.services;

import com.TakeHome.PZ.dto.UserResponseDTO;
import com.TakeHome.PZ.models.Enums.Theme;
import java.util.UUID;

public interface UserService {
    UserResponseDTO createUser(String name, UUID familyId);
    void updateBackgroundImage(UUID userId, String backgroundImageUrl);
    void updateTheme(UUID userId, Theme theme);
}
