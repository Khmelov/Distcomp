package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.AuthorRegistrationRequest;
import com.example.demo.entity.Author;
import com.example.demo.entity.Role;
import com.example.demo.repository.AuthorRepository;
import com.example.demo.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.context.annotation.Profile;
import java.util.Map;

@RestController
@RequestMapping("/api/v2.0")
@Profile("docker")
public class AuthControllerV2 {

    private final AuthorRepository authorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthControllerV2(AuthorRepository authorRepository,
                            PasswordEncoder passwordEncoder,
                            JwtService jwtService) {
        this.authorRepository = authorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/authors")
    public ResponseEntity<Author> register(@RequestBody AuthorRegistrationRequest request) {
        if (authorRepository.findByLogin(request.getLogin()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login already exists");
        }
        Author author = new Author();
        author.setLogin(request.getLogin());
        author.setPassword(passwordEncoder.encode(request.getPassword()));
        author.setFirstname(request.getFirstname());
        author.setLastname(request.getLastname());
        try {
            author.setRole(Role.valueOf(request.getRole().toUpperCase()));
        } catch (IllegalArgumentException | NullPointerException e) {
            author.setRole(Role.CUSTOMER);
        }
        Author saved = authorRepository.save(author);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest request) {
        Author author = authorRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login or password"));
        if (!passwordEncoder.matches(request.getPassword(), author.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid login or password");
        }
        String token = jwtService.generateToken(author.getLogin(), author.getRole().name());
        return ResponseEntity.ok(Map.of("access_token", token));
    }
}