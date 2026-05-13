package com.distcomp.controller.login;

import com.distcomp.config.security.LoginRequest;
import com.distcomp.config.security.LoginResponse;
import com.distcomp.data.r2dbc.repository.user.UserReactiveRepository;
import com.distcomp.service.user.UserService;
import com.distcomp.utils.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v2.0/login")
@RequiredArgsConstructor
public class LoginController {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final UserReactiveRepository userReactiveRepository;

    @PostMapping
    public Mono<ResponseEntity<LoginResponse>> login(@Valid @RequestBody final LoginRequest request) {
        return userReactiveRepository.findByLogin(request.getLogin())
                .flatMap(user -> {
                    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
                    }
                    final String token = jwtUtil.generateToken(user.getLogin(), user.getRole().name());
                    final LoginResponse response = LoginResponse.builder()
                            .accessToken(token)
                            .tokenType("Bearer")
                            .build();
                    return Mono.just(ResponseEntity.ok(response));
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")));
    }
}