package com.example.springboot_backend.service;

import com.example.springboot_backend.dto.AuthResponse;
import com.example.springboot_backend.dto.LoginRequest;
import com.example.springboot_backend.dto.RegisterRequest;
import com.example.springboot_backend.exception.CustomException;
import com.example.springboot_backend.model.Role;
import com.example.springboot_backend.model.User;
import com.example.springboot_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public User registerUser(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Registration rejected — username already taken: {}", request.getUsername());
            throw new CustomException("Username already taken: " + request.getUsername(), HttpStatus.CONFLICT);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration rejected — email already registered: {}", request.getEmail());
            throw new CustomException("Email already registered: " + request.getEmail(), HttpStatus.CONFLICT);
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // BCrypt hash, never stored/returned in plaintext
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(Role.USER);

        User saved = userRepository.save(user);
        log.info("Registered new user '{}' with role {}", saved.getUsername(), saved.getRole());
        return saved;
    }

    public AuthResponse loginUser(LoginRequest request) {
        // AuthenticationManager delegates to DaoAuthenticationProvider, which uses
        // CustomUserDetailsService + PasswordEncoder.matches() under the hood.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException("User not found: " + request.getUsername(), HttpStatus.NOT_FOUND));

        String token = jwtService.generateToken(authentication);
        log.info("User '{}' logged in successfully", user.getUsername());

        return new AuthResponse(token, user.getUsername(), user.getEmail(), user.getRole().name());
    }
}