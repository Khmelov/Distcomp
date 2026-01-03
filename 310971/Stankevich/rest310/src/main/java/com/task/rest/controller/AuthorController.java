package com.task.rest.controller;

import com.task.rest.dto.AuthorRequestTo;
import com.task.rest.dto.AuthorResponseTo;
import com.task.rest.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService service;

    @PostMapping
    public ResponseEntity<AuthorResponseTo> create(@Valid @RequestBody AuthorRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<AuthorResponseTo>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping
    public ResponseEntity<AuthorResponseTo> update(@Valid @RequestBody AuthorRequestTo request) {
        return ResponseEntity.ok(service.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<AuthorResponseTo> getByTweetId(@PathVariable Long tweetId) {
        return ResponseEntity.ok(service.getByTweetId(tweetId));
    }
}