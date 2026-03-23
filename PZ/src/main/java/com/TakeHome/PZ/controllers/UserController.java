package com.TakeHome.PZ.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import com.TakeHome.PZ.services.UserService;
import com.TakeHome.PZ.dto.UserBackgroundRequestDTO;
import com.TakeHome.PZ.dto.UserResponseDTO;
import com.TakeHome.PZ.dto.UserRequestDTO;
import com.TakeHome.PZ.dto.UserThemeRequestDTO;
import com.TakeHome.PZ.models.Enums.Theme;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO request) {
        return new ResponseEntity<>(userService.createUser(request.getName(), request.getFamilyId()), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/background")
    public ResponseEntity<Void> updateBackgroundImage(@PathVariable UUID id, @RequestBody UserBackgroundRequestDTO request) {
        userService.updateBackgroundImage(id, request.getBackgroundImageUrl());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/theme")
    public ResponseEntity<Void> updateTheme(@PathVariable UUID id, @RequestBody UserThemeRequestDTO request) {
        Theme theme = request.getTheme() == null ? Theme.LIGHT : request.getTheme();
        userService.updateTheme(id, theme);
        return ResponseEntity.noContent().build();
    }
}
