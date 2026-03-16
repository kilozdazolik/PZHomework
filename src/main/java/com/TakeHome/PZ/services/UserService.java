package com.TakeHome.PZ.services;

import com.TakeHome.PZ.dto.UserResponseDTO;
import java.util.UUID;

public interface UserService {
    UserResponseDTO createUser(String name, UUID familyId);
}
