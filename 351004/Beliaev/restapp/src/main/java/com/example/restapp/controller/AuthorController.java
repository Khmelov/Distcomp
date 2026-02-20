package com.example.restapp.controller;

import com.example.restapp.dto.request.AuthorRequestTo;
import com.example.restapp.dto.response.AuthorResponseTo;
import com.example.restapp.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/authors")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponseTo create(@Valid @RequestBody AuthorRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<AuthorResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public AuthorResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public AuthorResponseTo update(@PathVariable Long id, @Valid @RequestBody AuthorRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}