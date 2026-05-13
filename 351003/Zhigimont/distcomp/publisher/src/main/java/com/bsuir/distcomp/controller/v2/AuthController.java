package com.bsuir.distcomp.controller.v2;

import com.bsuir.distcomp.dto.*;
import com.bsuir.distcomp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2.0")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getLogin(), request.getPassword());
        return ResponseEntity.ok(new LoginResponse(token, "Bearer"));
    }
}