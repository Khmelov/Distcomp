package com.example.task361.controller.v2;

import com.example.task361.domain.dto.request.LoginRequestTo;
import com.example.task361.domain.dto.response.LoginResponseTo;
import com.example.task361.domain.dto.response.MeResponseTo;
import com.example.task361.domain.entity.Author;
import com.example.task361.repository.AuthorRepository;
import com.example.task361.security.JwtService;
import com.example.task361.security.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2.0")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthorRepository authorRepository;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponseTo login(@Valid @RequestBody LoginRequestTo request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword())
        );
        String login = auth.getName();
        Author author = authorRepository.findByLogin(login).orElseThrow();
        Role role = author.getRole();
        String token = jwtService.generateToken(login, role);
        return new LoginResponseTo(token, "Bearer");
    }

    @GetMapping("/me")
    public MeResponseTo me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Author author = authorRepository.findByLogin(auth.getName()).orElseThrow();
        return new MeResponseTo(author.getId(), author.getLogin(), author.getRole());
    }
}
