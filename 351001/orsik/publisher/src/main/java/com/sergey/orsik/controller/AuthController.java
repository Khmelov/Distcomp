package com.sergey.orsik.controller;

import com.sergey.orsik.dto.request.LoginRequestTo;
import com.sergey.orsik.dto.response.AuthTokenResponseTo;
import com.sergey.orsik.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2.0/login")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping
    public AuthTokenResponseTo login(@Valid @RequestBody LoginRequestTo request) {
        return authService.authenticate(request);
    }
}
