package com.TakeHome.PZ.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.TakeHome.PZ.dto.AiCommandRequestDTO;
import com.TakeHome.PZ.dto.AiCommandResponseDTO;
import com.TakeHome.PZ.services.AiCommandService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiCommandService aiCommandService;

    public AiController(AiCommandService aiCommandService) {
        this.aiCommandService = aiCommandService;
    }

    @PostMapping("/command")
    public ResponseEntity<AiCommandResponseDTO> interpretCommand(@Valid @RequestBody AiCommandRequestDTO request) {
        AiCommandResponseDTO response = aiCommandService.handlePrompt(request.getUserId(), request.getPrompt(), false);
        return ResponseEntity.ok(response);
    }
}
