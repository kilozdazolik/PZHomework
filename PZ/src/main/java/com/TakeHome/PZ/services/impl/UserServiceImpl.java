package com.TakeHome.PZ.services.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TakeHome.PZ.repository.FamilyRepository;
import com.TakeHome.PZ.repository.UserRepository;
import com.TakeHome.PZ.services.UserService;
import com.TakeHome.PZ.models.User;
import com.TakeHome.PZ.dto.UserResponseDTO;
import com.TakeHome.PZ.models.Enums.Role;
import com.TakeHome.PZ.models.Enums.Theme;
import com.TakeHome.PZ.models.Family;

@Service
public class UserServiceImpl implements UserService {
    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(FamilyRepository familyRepository, UserRepository userRepository) {
        this.familyRepository = familyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDTO createUser(String name, UUID familyId) {
    Family family = familyRepository.findById(familyId)
        .orElseThrow(() -> new IllegalStateException("No family found."));

    Role role = family.getMembers().isEmpty() ? Role.ADMIN : Role.USER;

    User user = User.builder()
        .name(name)
        .theme(Theme.LIGHT)
        .role(role)
        .family(family)
        .build();

    User savedUser = userRepository.save(user);
    family.getMembers().add(user);

    UserResponseDTO response = new UserResponseDTO();
    response.setId(savedUser.getId());
    response.setName(savedUser.getName());
    response.setRole(savedUser.getRole().name());
    response.setFamilyName(family.getName());
    return response;
    }

    @Override
    public void updateBackgroundImage(UUID userId, String backgroundImageUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId));

        String normalizedUrl = backgroundImageUrl == null ? null : backgroundImageUrl.trim();
        user.setBackgroundImageUrl((normalizedUrl == null || normalizedUrl.isEmpty()) ? null : normalizedUrl);
        userRepository.save(user);
    }

    @Override
    public void updateTheme(UUID userId, Theme theme) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId));

        user.setTheme(theme == null ? Theme.LIGHT : theme);
        userRepository.save(user);
    }
}
