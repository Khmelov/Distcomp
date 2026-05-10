package com.example.task310.controller.v2;

import com.example.task310.dto.CreatorRequestTo;
import com.example.task310.dto.CreatorResponseTo;
import com.example.task310.dto.LoginRequest;
import com.example.task310.dto.LoginResponse;
import com.example.task310.model.Role;
import com.example.task310.security.JwtService;
import com.example.task310.service.CreatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2.0")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CreatorService creatorService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        return ResponseEntity.ok(new LoginResponse(token, "Bearer"));
    }

    @PostMapping("/creators")
    public ResponseEntity<CreatorResponseTo> register(@Valid @RequestBody CreatorRequestTo request) {
        // Устанавливаем роль по умолчанию CUSTOMER, если не указана
        CreatorResponseTo response = creatorService.create(request, Role.CUSTOMER);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}