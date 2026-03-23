package com.TakeHome.PZ.services;

import java.util.Optional;
import java.util.UUID;

import com.TakeHome.PZ.models.Family;

public interface FamilyService {
    Optional<Family> findById(UUID id);
    Family saveFamily(String name);    
    void deleteFamily(UUID id);
    Family addUserToFamily(UUID familyId, UUID userId, String name);
    void removeUserFromFamily(UUID familyId, UUID userId);
}
