package com.coursework.jsontool.controller;

import com.coursework.jsontool.dto.UserRegistrationDto;
import com.coursework.jsontool.model.User;
import com.coursework.jsontool.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.coursework.jsontool.dto.UserResponseDto;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
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
}