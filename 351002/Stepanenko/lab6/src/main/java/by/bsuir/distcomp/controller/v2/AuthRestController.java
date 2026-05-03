package by.bsuir.distcomp.controller.v2;

import by.bsuir.distcomp.core.domain.Author;
import by.bsuir.distcomp.core.exception.ResourceNotFoundException;
import by.bsuir.distcomp.core.security.JwtTokenProvider;
import by.bsuir.distcomp.core.service.AuthorService;
import by.bsuir.distcomp.dto.request.AuthorRequestTo;
import by.bsuir.distcomp.dto.request.LoginRequestTo;
import by.bsuir.distcomp.dto.response.AuthorResponseTo;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v2.0")
public class AuthRestController {

    private final AuthorService authorService;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthRestController(AuthorService authorService, JwtTokenProvider tokenProvider, PasswordEncoder passwordEncoder) {
        this.authorService = authorService;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/authors")
    public ResponseEntity<AuthorResponseTo> register(@Valid @RequestBody AuthorRequestTo request) {
        return new ResponseEntity<>(authorService.create(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestTo request) {
        try {
            Author author = authorService.getEntityByLogin(request.getLogin());
            if (passwordEncoder.matches(request.getPassword(), author.getPassword())) {
                String token = tokenProvider.createToken(author.getLogin(), author.getRole().name());
                return ResponseEntity.ok(Map.of("access_token", token, "type_token", "Bearer"));
            }
        } catch (ResourceNotFoundException e) {
            // Если пользователя нет - возвращаем 401, как просит тест
        }
        return new ResponseEntity<>(Map.of("errorMessage", "Invalid credentials", "errorCode", "40101"), HttpStatus.UNAUTHORIZED);
    }
}