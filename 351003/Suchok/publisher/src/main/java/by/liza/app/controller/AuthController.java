package by.liza.app.controller;

import by.liza.app.dto.request.LoginRequestTo;
import by.liza.app.dto.response.LoginResponseTo;
import by.liza.app.repository.WriterRepository;
import by.liza.app.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v2.0")
@RequiredArgsConstructor
public class AuthController {

    private final WriterRepository writerRepository;
    private final PasswordEncoder  passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestTo req) {
        return writerRepository.findByLogin(req.login())
                .filter(w -> passwordEncoder.matches(req.password(), w.getPassword()))
                .map(w -> ResponseEntity.ok((Object) new LoginResponseTo(
                        jwtTokenProvider.generateToken(w.getLogin(), w.getRole().name()),
                        "Bearer")))
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of(
                        "errorMessage", "Invalid login or password",
                        "errorCode",    40101)));
    }
}
