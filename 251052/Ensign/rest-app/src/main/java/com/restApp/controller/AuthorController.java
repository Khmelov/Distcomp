package com.restApp.controller;

import com.restApp.dto.AuthorRequestTo;
import com.restApp.dto.AuthorResponseTo;
import com.restApp.service.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<AuthorResponseTo> create(@RequestBody AuthorRequestTo request) {
        return new ResponseEntity<>(authorService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AuthorResponseTo>> getAll() {
        return ResponseEntity.ok(authorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseTo> update(@PathVariable Long id, @RequestBody AuthorRequestTo request) {
        return ResponseEntity.ok(authorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        authorService.delete(id);
    }
}
