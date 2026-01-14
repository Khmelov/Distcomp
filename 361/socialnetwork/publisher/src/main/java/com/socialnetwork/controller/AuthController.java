package com.socialnetwork.controller;

import com.socialnetwork.dto.request.LoginRequest;
import com.socialnetwork.dto.request.RegisterRequest;
import com.socialnetwork.dto.response.LoginResponse;
import com.socialnetwork.dto.response.UserResponseTo;
import com.socialnetwork.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2.0")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/users")
    public ResponseEntity<UserResponseTo> register(@Valid @RequestBody RegisterRequest request) {
        UserResponseTo createdUser = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}

