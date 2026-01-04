package com.task.rest.controller;

import com.task.rest.dto.AuthorRequestTo;
import com.task.rest.dto.AuthorResponseTo;
import com.task.rest.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1.0/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseTo> getById(@PathVariable Long id) {
        log.info("GET request for author with id: {}", id);
        return ResponseEntity.ok(authorService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<AuthorResponseTo>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET request for all authors");
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(authorService.getAll(pageable).getContent());
    }

    @PostMapping
    public ResponseEntity<AuthorResponseTo> create(@Valid @RequestBody AuthorRequestTo requestTo) {
        log.info("POST request to create author: {}", requestTo.getLogin());
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.create(requestTo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody AuthorRequestTo requestTo) {
        log.info("PUT request to update author with id: {}", id);
        return ResponseEntity.ok(authorService.update(id, requestTo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE request for author with id: {}", id);
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
