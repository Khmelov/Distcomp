package com.example.publisher.controller;

import com.example.publisher.dto.request.AuthorRequestTo;
import com.example.publisher.dto.response.AuthorResponseTo;
import com.example.publisher.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/authors")
@RequiredArgsConstructor
public class AuthorControllerV2 {
    private final AuthorService service;

    // Регистрация доступна всем (прописано в SecurityConfig)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponseTo create(@Valid @RequestBody AuthorRequestTo request) {
        return service.create(request);
    }

    // Читать могут все аутентифицированные (прописано в SecurityConfig)
    @GetMapping
    public List<AuthorResponseTo> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public AuthorResponseTo getById(@PathVariable Long id) { return service.getById(id); }

    // Изменять может Админ или сам автор своего профиля
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityEvaluator.isSelf(#id, authentication.name)")
    public AuthorResponseTo update(@PathVariable Long id, @Valid @RequestBody AuthorRequestTo request) {
        return service.update(id, request);
    }

    // Удалять может Админ или сам автор своего профиля
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @securityEvaluator.isSelf(#id, authentication.name)")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}