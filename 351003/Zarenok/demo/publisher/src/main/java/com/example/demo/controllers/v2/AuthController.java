package com.example.demo.controllers.v2;

import com.example.demo.dto.requests.AuthorRequestTo;
import com.example.demo.dto.requests.LoginRequest;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.model.Author;
import com.example.demo.service.AuthorService;
import com.example.demo.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v2.0")
public class AuthController {

    private final AuthorService authorService;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthorService authorService, JwtUtil jwtUtil, BCryptPasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager) {
        this.authorService = authorService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/authors")
    public ResponseEntity<AuthorResponseTo> register(@Valid @RequestBody AuthorRequestTo dto) {
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getRole() == null || dto.getRole().trim().isEmpty()) {
            dto.setRole("CUSTOMER");
        }
        AuthorResponseTo created = authorService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
            );
            String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
            String token = jwtUtil.generateToken(request.getLogin(), role);
            return ResponseEntity.ok(Map.of("access_token", token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Internal error"));
        }
    }
}
