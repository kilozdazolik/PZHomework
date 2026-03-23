package com.TakeHome.PZ.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.TakeHome.PZ.dto.ApplicationResponseDTO;
import com.TakeHome.PZ.models.Application;
import com.TakeHome.PZ.models.User;
import com.TakeHome.PZ.repository.ApplicationRepository;
import com.TakeHome.PZ.repository.UserRepository;
import com.TakeHome.PZ.services.ApplicationService;

@Service
public class ApplicationServiceImpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    @Autowired
    public ApplicationServiceImpl(ApplicationRepository applicationRepository, UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ApplicationResponseDTO> listByUserId(UUID userId) {
        return applicationRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ApplicationResponseDTO addApplication(String name, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId));

        Application application = new Application();
        application.setName(name.trim());
        application.setUser(user);

        Application savedApplication = applicationRepository.save(application);
        return toResponse(savedApplication);
    }

    @Override
    public ApplicationResponseDTO updateApplication(String id, String name, UUID userId) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Application not found: " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found: " + userId));

        application.setName(name.trim());
        application.setUser(user);

        Application updatedApplication = applicationRepository.save(application);
        return toResponse(updatedApplication);
    }

    @Override
    public void deleteApplication(String id) {
        if (!applicationRepository.existsById(id)) {
            throw new IllegalStateException("Application not found: " + id);
        }
        applicationRepository.deleteById(id);
    }

    private ApplicationResponseDTO toResponse(Application application) {
        return new ApplicationResponseDTO(
                application.getId(),
                application.getName(),
                application.getUser().getId());
    }
}