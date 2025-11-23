package com.coursework.jsontool.controller;

import com.coursework.jsontool.model.SystemLog;
import com.coursework.jsontool.model.User;
import com.coursework.jsontool.repository.SystemLogRepository;
import com.coursework.jsontool.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final SystemLogRepository logRepository;

    public AdminController(UserRepository userRepository, SystemLogRepository logRepository) {
        this.userRepository = userRepository;
        this.logRepository = logRepository;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/logs")
    public ResponseEntity<List<SystemLog>> getLogs() {
        return ResponseEntity.ok(logRepository.findTop50ByOrderByTimestampDesc());
    }

    @PostMapping("/users/{userId}/ban")
    public ResponseEntity<?> banUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setRole("BANNED");
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}