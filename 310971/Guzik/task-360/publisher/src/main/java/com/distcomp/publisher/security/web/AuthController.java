package com.distcomp.publisher.security.web;

import com.distcomp.publisher.security.JwtService;
import com.distcomp.publisher.security.dto.LoginRequest;
import com.distcomp.publisher.security.dto.TokenResponse;
import com.distcomp.publisher.writer.domain.Writer;
import com.distcomp.publisher.writer.repo.WriterRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2.0")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final WriterRepository writerRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, WriterRepository writerRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.writerRepository = writerRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );

        String login = authentication.getName();
        Writer writer = writerRepository.findByLogin(login).orElseThrow();

        String token = jwtService.generateToken(writer.getLogin(), writer.getRole());
        return ResponseEntity.ok(new TokenResponse(token));
    }
}
