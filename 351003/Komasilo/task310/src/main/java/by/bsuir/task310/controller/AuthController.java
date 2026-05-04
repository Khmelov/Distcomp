package by.bsuir.task310.controller;

import by.bsuir.task310.dto.LoginRequestTo;
import by.bsuir.task310.dto.LoginResponseTo;
import by.bsuir.task310.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2.0")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponseTo login(@RequestBody LoginRequestTo requestTo) {
        return service.login(requestTo);
    }
}