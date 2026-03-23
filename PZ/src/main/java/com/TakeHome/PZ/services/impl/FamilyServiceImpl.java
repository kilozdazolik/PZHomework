package com.TakeHome.PZ.services.impl;

import com.TakeHome.PZ.models.Family;
import com.TakeHome.PZ.models.Enums.Role;
import com.TakeHome.PZ.models.Enums.Theme;
import com.TakeHome.PZ.models.User;
import com.TakeHome.PZ.repository.FamilyRepository;
import com.TakeHome.PZ.repository.UserRepository;
import com.TakeHome.PZ.services.FamilyService;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class FamilyServiceImpl implements FamilyService {
    private final FamilyRepository familyRepository;
    private final UserRepository userRepository;

    @Autowired
    public FamilyServiceImpl(FamilyRepository familyRepository, UserRepository userRepository) {
        this.familyRepository = familyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<Family> findById(UUID id) {
        return familyRepository.findById(id);
    }

    @Override
    public Family saveFamily(String name) {
        Family family = Family.builder().name(name).build();
        return familyRepository.save(family);
    }

    @Override
    public void deleteFamily(UUID id) {
        familyRepository.deleteById(id);
    }

    @Transactional
    @Override
    public Family addUserToFamily(UUID familyId, UUID userId, String name) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new IllegalStateException("Family not found: " + familyId));

        User userToAdd;
        if (name != null && !name.isBlank()) {
            Role role = family.getMembers().isEmpty() ? Role.ADMIN : Role.USER;
            userToAdd = User.builder()
                    .name(name.trim())
                    .role(role)
                    .theme(Theme.LIGHT)
                    .family(family)
                    .build();
        } else if (userId != null) {
            userToAdd = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalStateException("User not found: " + userId));
            userToAdd.setFamily(family);
        } else {
            throw new IllegalStateException("Either userId or name is required to add a member.");
        }

        User savedUser = userRepository.save(userToAdd);
        family.getMembers().add(savedUser);
        return family;
    }

    @Override
    public void removeUserFromFamily(UUID familyId, UUID userId) {
        if (!familyRepository.existsById(familyId)) {
            throw new IllegalStateException("Family not found: " + familyId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId));

        if (user.getFamily() != null && familyId.equals(user.getFamily().getId())) {
            user.setFamily(null);
            userRepository.save(user);
        }
    }
}
