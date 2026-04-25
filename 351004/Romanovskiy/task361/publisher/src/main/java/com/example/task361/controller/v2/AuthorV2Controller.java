package com.example.task361.controller.v2;

import com.example.task361.domain.dto.request.AuthorRequestTo;
import com.example.task361.domain.dto.response.AuthorResponseTo;
import com.example.task361.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/authors")
@RequiredArgsConstructor
public class AuthorV2Controller {
    private final AuthorService authorService;

    // Регистрация (permitAll задаётся в SecurityConfig)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponseTo create(@Valid @RequestBody AuthorRequestTo request) {
        return authorService.create(request);
    }

    @GetMapping("/{id}")
    public AuthorResponseTo findById(@PathVariable Long id) {
        return authorService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @ownershipService.isSelf(#id))")
    public AuthorResponseTo update(@PathVariable Long id, @Valid @RequestBody AuthorRequestTo request) {
        request.setId(id);
        return authorService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @ownershipService.isSelf(#id))")
    public void deleteById(@PathVariable Long id) {
        authorService.deleteById(id);
    }

    @GetMapping
    public List<AuthorResponseTo> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return authorService.findAll(page, size);
    }
}
