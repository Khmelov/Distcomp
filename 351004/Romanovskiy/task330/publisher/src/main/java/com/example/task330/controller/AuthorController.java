package com.example.task330.controller;

import com.example.task330.domain.dto.request.AuthorRequestTo;
import com.example.task330.domain.dto.response.AuthorResponseTo;
import com.example.task330.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201 Created
    public AuthorResponseTo create(@Valid @RequestBody AuthorRequestTo request) {
        return authorService.create(request);
    }

    @GetMapping("/{id}")
    public AuthorResponseTo findById(@PathVariable Long id) {
        return authorService.findById(id);
    }

    @PutMapping
    public AuthorResponseTo update(@Valid @RequestBody AuthorRequestTo request) {
        return authorService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content
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