package com.example.task310.controller;

import com.example.task310.dto.LoginRequestTo;
import com.example.task310.entity.Writer;
import com.example.task310.security.JwtTokenProvider;
import com.example.task310.service.WriterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v2.0")
@RequiredArgsConstructor
public class AuthController {
    private final WriterService writerService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestTo request) {
        try {
            Writer writer = writerService.getEntityByLogin(request.getLogin());
            if (writerService.verifyPassword(request.getPassword(), writer.getPassword())) {
                String token = tokenProvider.createToken(writer.getLogin(), writer.getRole().name());
                return ResponseEntity.ok(Map.of("access_token", token, "type_token", "Bearer"));
            }
        } catch (RuntimeException ignored) {
        }
        return new ResponseEntity<>(Map.of("errorMessage", "Invalid credentials", "errorCode", "40101"), HttpStatus.UNAUTHORIZED);
    }
}
