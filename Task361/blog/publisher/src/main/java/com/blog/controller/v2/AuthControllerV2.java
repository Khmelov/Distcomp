package com.blog.controller.v2;

import com.blog.dto.request.EditorRequestTo;
import com.blog.dto.request.LoginRequest;
import com.blog.dto.response.EditorResponseTo;
import com.blog.dto.response.LoginResponse;
import com.blog.service.EditorService;
import com.blog.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2.0")
public class AuthControllerV2 {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private EditorService editorService;

    // Регистрация нового пользователя
    @PostMapping("/editors")
    public ResponseEntity<EditorResponseTo> register(@Valid @RequestBody EditorRequestTo request) {
        // Вызываем существующий сервис создания редактора
        EditorResponseTo response = editorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Аутентификация и получение JWT токена
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        // Аутентификация пользователя
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(),
                        request.getPassword()
                )
        );

        // Загружаем UserDetails для генерации токена
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getLogin());

        // Генерируем JWT токен
        String jwtToken = jwtService.generateToken(userDetails);

        // Извлекаем роль из authorities
        String role = userDetails.getAuthorities().stream()
                .filter(auth -> auth.getAuthority().startsWith("ROLE_"))
                .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                .findFirst()
                .orElse("CUSTOMER");

        // Создаем ответ
        LoginResponse loginResponse = new LoginResponse(
                jwtToken,
                role,
                request.getLogin(),
                jwtService.getJwtExpiration()
        );

        return ResponseEntity.ok(loginResponse);
    }

    // Endpoint для проверки JWT токена (опционально)
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("{\"error\": \"No valid token provided\"}");
        }

        String token = authHeader.substring(7);
        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);

        return ResponseEntity.ok(
                String.format("{\"username\": \"%s\", \"role\": \"%s\", \"valid\": true}", username, role)
        );
    }
}