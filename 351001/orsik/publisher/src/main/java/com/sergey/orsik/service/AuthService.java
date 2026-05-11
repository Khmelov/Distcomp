package com.sergey.orsik.service;

import com.sergey.orsik.dto.request.LoginRequestTo;
import com.sergey.orsik.dto.response.AuthTokenResponseTo;
import com.sergey.orsik.entity.Creator;
import com.sergey.orsik.repository.CreatorRepository;
import com.sergey.orsik.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CreatorRepository creatorRepository;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            CreatorRepository creatorRepository,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.creatorRepository = creatorRepository;
        this.jwtService = jwtService;
    }

    public AuthTokenResponseTo authenticate(LoginRequestTo request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
            );
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(UNAUTHORIZED, "Invalid credentials");
        }
        Creator creator = creatorRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, "Invalid credentials"));
        String token = jwtService.generateToken(creator.getLogin(), creator.getRole());
        return new AuthTokenResponseTo(token, "Bearer");
    }
}
