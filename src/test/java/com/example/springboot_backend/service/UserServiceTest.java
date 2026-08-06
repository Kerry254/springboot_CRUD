package com.example.springboot_backend.service;

import com.example.springboot_backend.dto.RegisterRequest;
import com.example.springboot_backend.exception.CustomException;
import com.example.springboot_backend.model.Role;
import com.example.springboot_backend.model.User;
import com.example.springboot_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void registerUser_hashesPasswordAndSaves() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("victor");
        request.setEmail("victor@example.com");
        request.setPassword("plaintext123");
        request.setFirstName("Victor");
        request.setLastName("Kerry");

        when(userRepository.existsByUsername("victor")).thenReturn(false);
        when(userRepository.existsByEmail("victor@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plaintext123")).thenReturn("$2a$10$hashedvalue");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.registerUser(request);

        assertThat(result.getPassword()).isEqualTo("$2a$10$hashedvalue");
        assertThat(result.getPassword()).isNotEqualTo("plaintext123");
        assertThat(result.getRole()).isEqualTo(Role.USER);
        verify(passwordEncoder).encode("plaintext123");
    }

    @Test
    void registerUser_duplicateUsername_throwsConflict() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("victor");
        request.setEmail("victor@example.com");
        request.setPassword("plaintext123");

        when(userRepository.existsByUsername("victor")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT));

        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_duplicateEmail_throwsConflict() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setEmail("taken@example.com");
        request.setPassword("plaintext123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getStatus()).isEqualTo(HttpStatus.CONFLICT));

        verify(userRepository, never()).save(any());
    }
}
