package org.example.newsapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.newsapi.config.JwtTokenProvider;
import org.example.newsapi.dto.request.LoginRequest;
import org.example.newsapi.dto.request.UserRegistrationRequest;
import org.example.newsapi.dto.response.UserResponseTo;
import org.example.newsapi.entity.User;
import org.example.newsapi.exception.AlreadyExistsException;
import org.example.newsapi.mapper.UserMapper;
import org.example.newsapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v2.0")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );
        String role = authentication.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        String token = tokenProvider.generateToken(request.getLogin(), role);
        return ResponseEntity.ok(Map.of("access_token", token));
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseTo register(@RequestBody @Valid UserRegistrationRequest request) {
        if (userRepository.existsByLogin(request.getLogin())) {
            throw new AlreadyExistsException("Login already exists");
        }
        User user = User.builder()
                .login(request.getLogin())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .role(request.getRole() != null ? request.getRole() : "CUSTOMER")
                .build();
        user = userRepository.save(user);
        return userMapper.toDto(user);
    }
}