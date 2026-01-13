package com.publick.controller;

import com.publick.dto.AuthorRequestTo;
import com.publick.dto.AuthorResponseTo;
import com.publick.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/authors")
@Validated
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping
    public ResponseEntity<List<AuthorResponseTo>> getAllAuthors() {
        List<AuthorResponseTo> authors = authorService.getAll();
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseTo> getAuthorById(@PathVariable Long id) {
        AuthorResponseTo author = authorService.getById(id);
        return ResponseEntity.ok(author);
    }

    @PostMapping
    public ResponseEntity<AuthorResponseTo> createAuthor(@Valid @RequestBody AuthorRequestTo request) {
        AuthorResponseTo created = authorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseTo> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorRequestTo request) {
        AuthorResponseTo updated = authorService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}