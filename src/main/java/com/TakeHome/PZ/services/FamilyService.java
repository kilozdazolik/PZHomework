package com.TakeHome.PZ.services;

import java.util.Optional;
import java.util.UUID;

import com.TakeHome.PZ.models.Family;
import com.TakeHome.PZ.models.User;

public interface FamilyService {
    Optional<Family> findById(UUID id);
    Family saveFamily(String name);    
    void deleteFamily(UUID id);
    void addUserToFamily(UUID familyId, User user);
    void removeUserFromFamily(UUID familyId, UUID userId);
}
