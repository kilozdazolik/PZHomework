package com.TakeHome.PZ.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import com.TakeHome.PZ.services.UserService;
import com.TakeHome.PZ.dto.UserResponseDTO;
import com.TakeHome.PZ.dto.UserRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;


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
}
