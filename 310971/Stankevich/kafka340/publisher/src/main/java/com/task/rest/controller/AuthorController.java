package com.task.rest.controller;

import com.task.rest.dto.AuthorRequestTo;
import com.task.rest.dto.AuthorResponseTo;
import com.task.rest.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseTo> getById(@PathVariable Long id) {
        log.info("GET /api/v1.0/authors/{}", id);
        return ResponseEntity.ok(authorService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<AuthorResponseTo>> getAll() {
        log.info("GET /api/v1.0/authors");
        return ResponseEntity.ok(authorService.getAllList());
    }

    @PostMapping
    public ResponseEntity<AuthorResponseTo> create(@Valid @RequestBody AuthorRequestTo request) {
        log.info("POST /api/v1.0/authors");
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody AuthorRequestTo request) {
        log.info("PUT /api/v1.0/authors/{}", id);
        return ResponseEntity.ok(authorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1.0/authors/{}", id);
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
