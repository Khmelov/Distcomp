package com.example.lab.publisher.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lab.publisher.exception.GlobalExceptionHandler.ErrorResponse;
import com.example.lab.publisher.model.User;
import com.example.lab.publisher.repository.UserRepository;
import com.example.lab.publisher.security.AuthDtos.CurrentUserResponse;
import com.example.lab.publisher.security.AuthDtos.LoginRequest;
import com.example.lab.publisher.security.AuthDtos.RegisterRequest;
import com.example.lab.publisher.security.AuthDtos.TokenResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2.0")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/users")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.findByLogin(req.getLogin()).isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(40301, "Duplicate entry"));
        }

        User user = new User();
        user.setLogin(req.getLogin());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setFirstname(req.getFirstname());
        user.setLastname(req.getLastname());
        user.setRole(req.getRole());

        User saved = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CurrentUserResponse(saved.getId(), saved.getLogin(), saved.getFirstname(),
                        saved.getLastname(), saved.getRole()));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getLogin(), req.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = userRepository.findByLogin(req.getLogin()).orElseThrow();
        String token = jwtService.issueToken(user.getLogin(), user.getRole() == null ? Role.CUSTOMER : user.getRole());
        return ResponseEntity.ok(new TokenResponse(token));
    }

    @GetMapping("/users/me")
    public ResponseEntity<CurrentUserResponse> me() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByLogin(login).orElseThrow();
        return ResponseEntity
                .ok(new CurrentUserResponse(user.getId(), user.getLogin(), user.getFirstname(), user.getLastname(),
                        user.getRole() == null ? Role.CUSTOMER : user.getRole()));
    }
}
