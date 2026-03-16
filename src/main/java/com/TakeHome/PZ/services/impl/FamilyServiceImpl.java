package com.TakeHome.PZ.services.impl;

import com.TakeHome.PZ.models.Family;
import com.TakeHome.PZ.models.User;
import com.TakeHome.PZ.repository.FamilyRepository;
import com.TakeHome.PZ.repository.UserRepository;
import com.TakeHome.PZ.services.FamilyService;
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

    @Override
    public Family addUserToFamily(UUID familyId, UUID userId) {
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new IllegalArgumentException("Family not found: " + familyId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        user.setFamily(family);
        User savedUser = userRepository.save(user);
        family.getMembers().add(savedUser);
        return family;
    }

    @Override
    public void removeUserFromFamily(UUID familyId, UUID userId) {
        if (!familyRepository.existsById(familyId)) {
            throw new IllegalArgumentException("Family not found: " + familyId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        if (user.getFamily() != null && familyId.equals(user.getFamily().getId())) {
            user.setFamily(null);
            userRepository.save(user);
        }
    }
}
