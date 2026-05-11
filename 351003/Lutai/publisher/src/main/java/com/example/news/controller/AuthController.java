package com.example.news.controller;

import com.example.common.dto.LoginRequest;
import com.example.common.dto.JwtResponse;
import com.example.common.dto.WriterRequestTo;
import com.example.common.dto.WriterResponseTo;
import com.example.news.security.JwtTokenProvider;
import com.example.news.service.WriterService;
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
    private final JwtTokenProvider tokenProvider;
    private final WriterService writerService;

    @PostMapping("/writers")
    public ResponseEntity<WriterResponseTo> register(@RequestBody WriterRequestTo request) {
        WriterResponseTo response = writerService.create(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.login(),
                        loginRequest.password()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String roleStr = userDetails.getAuthorities().iterator().next()
                .getAuthority().replace("ROLE_", "");

        String jwt = tokenProvider.generateToken(
                userDetails.getUsername(),
                com.example.common.dto.model.enums.Role.valueOf(roleStr)
        );

        return ResponseEntity.ok(new JwtResponse(jwt));
    }
}