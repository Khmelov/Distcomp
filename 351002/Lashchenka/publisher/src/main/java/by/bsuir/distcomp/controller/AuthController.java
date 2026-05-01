package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.LoginRequestTo;
import by.bsuir.distcomp.dto.response.LoginResponseTo;
import by.bsuir.distcomp.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2.0")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseTo> login(@Valid @RequestBody LoginRequestTo request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
