package com.TakeHome.PZ.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.TakeHome.PZ.dto.ApplicationRequestDTO;
import com.TakeHome.PZ.dto.ApplicationResponseDTO;
import com.TakeHome.PZ.services.ApplicationService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/apps")
public class ApplicationController {
    private final ApplicationService applicationService;

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ApplicationResponseDTO>> listByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(applicationService.listByUserId(userId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApplicationResponseDTO> addApplication(@Valid @RequestBody ApplicationRequestDTO request) {
        ApplicationResponseDTO created = applicationService.addApplication(request.getName(), request.getUserId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponseDTO> updateApplication(
            @PathVariable String id,
            @Valid @RequestBody ApplicationRequestDTO request) {
        ApplicationResponseDTO updated = applicationService.updateApplication(id, request.getName(), request.getUserId());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable String id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.noContent().build();
    }
}