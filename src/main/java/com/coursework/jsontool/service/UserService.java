package com.coursework.jsontool.service;

import com.coursework.jsontool.model.User;
import com.coursework.jsontool.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LogService logService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, LogService logService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.logService = logService;
    }

    public User save(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);
        user.setRole("USER");
        logService.log("INFO", "UserService", "New user registered: " + user.getEmail());
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}