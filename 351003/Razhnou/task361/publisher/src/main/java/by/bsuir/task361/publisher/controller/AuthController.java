package by.bsuir.task361.publisher.controller;

import by.bsuir.task361.publisher.dto.request.LoginRequestTo;
import by.bsuir.task361.publisher.dto.response.LoginResponseTo;
import by.bsuir.task361.publisher.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2.0/login")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public LoginResponseTo login(@Valid @RequestBody LoginRequestTo request) {
        return authenticationService.login(request);
    }
}
