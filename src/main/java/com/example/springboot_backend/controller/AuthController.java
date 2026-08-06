package com.example.springboot_backend.controller;

import com.example.springboot_backend.dto.LoginRequest;
import com.example.springboot_backend.dto.RegisterRequest;
import com.example.springboot_backend.dto.UserResponse;
import com.example.springboot_backend.dto.AuthResponse;
import com.example.springboot_backend.model.User;
import com.example.springboot_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

  @PostMapping("/register")
public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody RegisterRequest request) {
    log.info("Received registration request for username: {}", request.getUsername());
    User user = userService.registerUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromUser(user));
}

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request for username: {}", request.getUsername());
        AuthResponse response = userService.loginUser(request);
        return ResponseEntity.ok(response);
    }
}
