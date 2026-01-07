package com.task310.blogplatform.controller.v2;

import com.task310.blogplatform.dto.UserResponseTo;
import com.task310.blogplatform.dto.auth.LoginRequest;
import com.task310.blogplatform.dto.auth.LoginResponse;
import com.task310.blogplatform.dto.auth.RegisterRequest;
import com.task310.blogplatform.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2.0")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponseTo> register(@RequestBody RegisterRequest request) {
        UserResponseTo created = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}

