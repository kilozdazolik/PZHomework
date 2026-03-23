package com.TakeHome.PZ.services;

import java.util.List;
import java.util.UUID;

import com.TakeHome.PZ.dto.ApplicationResponseDTO;

public interface ApplicationService {
    List<ApplicationResponseDTO> listByUserId(UUID userId);
    ApplicationResponseDTO addApplication(String name, UUID userId);
    ApplicationResponseDTO updateApplication(String id, String name, UUID userId);
    void deleteApplication(String id);
}