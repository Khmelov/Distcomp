package com.example.kafkademo.controller.v2;

import com.example.kafkademo.config.JwtTokenProvider;
import com.example.kafkademo.dto.request.CreatorRequestDto;
import com.example.kafkademo.dto.request.LoginRequestDto;
import com.example.kafkademo.dto.response.AuthResponseDto;
import com.example.kafkademo.dto.response.RegistrationResponseDto;
import com.example.kafkademo.entity.Creator;
import com.example.kafkademo.entity.Role;
import com.example.kafkademo.service.CreatorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2.0")
public class AuthControllerV2 {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CreatorService creatorService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getLogin(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Creator creator = creatorService.findByLogin(loginRequest.getLogin()).get();
        String token = tokenProvider.generateToken(creator.getLogin(), creator.getRole());
        return ResponseEntity.ok(new AuthResponseDto(token));
    }

    @PostMapping("/creators")
    public ResponseEntity<RegistrationResponseDto> register(@Valid @RequestBody CreatorRequestDto request) {
        if (creatorService.existsByLogin(request.getLogin())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Creator creator = new Creator();
        creator.setLogin(request.getLogin());
        creator.setPassword(passwordEncoder.encode(request.getPassword()));
        creator.setFirstName(request.getFirstName());
        creator.setLastName(request.getLastName());

        if (request.getRole() != null && !request.getRole().isEmpty()) {
            try {
                creator.setRole(Role.valueOf(request.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                creator.setRole(Role.CUSTOMER);
            }
        } else {
            creator.setRole(Role.CUSTOMER);
        }

        Creator saved = creatorService.save(creator);

        return ResponseEntity.status(HttpStatus.CREATED).body(new RegistrationResponseDto(saved));
    }
}