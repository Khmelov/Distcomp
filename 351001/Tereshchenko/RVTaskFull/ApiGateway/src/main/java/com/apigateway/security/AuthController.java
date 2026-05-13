package com.apigateway.security;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v2.0")
public class AuthController {

    public static final String AUTH_USER_ATTRIBUTE = "authUser";

    private final WriterIdentityClient writerIdentityClient;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(WriterIdentityClient writerIdentityClient, JwtService jwtService) {
        this.writerIdentityClient = writerIdentityClient;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(@RequestBody LoginRequest request) {
        if (request == null || request.getLogin() == null || request.getPassword() == null) {
            return Mono.just(error(HttpStatus.BAD_REQUEST, "Login and password are required", "40001"));
        }

        return writerIdentityClient.findByLogin(request.getLogin())
                .<ResponseEntity<?>>map(writer -> {
                    if (!passwordEncoder.matches(request.getPassword(), writer.getPassword())) {
                        return error(HttpStatus.UNAUTHORIZED, "Invalid login or password", "40101");
                    }

                    String role = writer.getRole() == null ? "CUSTOMER" : writer.getRole();
                    String token = jwtService.createToken(writer.getLogin(), role);
                    return ResponseEntity.ok(new LoginResponse(token, "Bearer"));
                })
                .onErrorResume(error -> Mono.just(error(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid login or password",
                        "40101"
                )));
    }

    @GetMapping("/me")
    public ResponseEntity<?> currentUser(ServerWebExchange exchange) {
        AuthUser user = exchange.getAttribute(AUTH_USER_ATTRIBUTE);
        if (user == null) {
            return error(HttpStatus.UNAUTHORIZED, "Authentication is required", "40101");
        }

        return ResponseEntity.ok(Map.of(
                "id", user.id(),
                "login", user.login(),
                "firstname", user.firstname(),
                "lastname", user.lastname(),
                "role", user.role()
        ));
    }

    @GetMapping("/me/role")
    public ResponseEntity<?> currentUserRole(ServerWebExchange exchange) {
        AuthUser user = exchange.getAttribute(AUTH_USER_ATTRIBUTE);
        if (user == null) {
            return error(HttpStatus.UNAUTHORIZED, "Authentication is required", "40101");
        }

        return ResponseEntity.ok(Map.of("role", user.role()));
    }

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String message, String code) {
        return ResponseEntity.status(status).body(new ErrorResponse(message, code));
    }
}
