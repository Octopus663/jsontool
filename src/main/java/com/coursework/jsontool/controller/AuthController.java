package com.coursework.jsontool.controller;

import com.coursework.jsontool.dto.UserRegistrationDto;
import com.coursework.jsontool.model.User;
import com.coursework.jsontool.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.coursework.jsontool.dto.UserResponseDto;
import com.coursework.jsontool.service.LogService;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final LogService logService;

    public AuthController(UserService userService, LogService logService) {
        this.userService = userService;
        this.logService = logService;
    }
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserRegistrationDto registrationDto) {

        if (userService.findByEmail(registrationDto.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        User newUser = new User(
                registrationDto.getEmail(),
                registrationDto.getPassword(),
                "USER"
        );

        User savedUser = userService.save(newUser);

        return ResponseEntity.ok(UserResponseDto.fromEntity(savedUser));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> getUserProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found."));
        logService.log("INFO", "AuthService", "User logged in: " + email);

        return ResponseEntity.ok(UserResponseDto.fromEntity(user));
    }

}