package com.publick.controller;

import com.publick.dto.AuthorRequestTo;
import com.publick.dto.AuthorResponseTo;
import com.publick.service.AuthorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/authors")
@Validated
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping
    public ResponseEntity<List<AuthorResponseTo>> getAllAuthors() {
        List<AuthorResponseTo> authors = authorService.getAll();
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<AuthorResponseTo>> getAllAuthorsPaged(@PageableDefault(size = 10) Pageable pageable) {
        Page<AuthorResponseTo> authors = authorService.getAll(pageable);
        return ResponseEntity.ok(authors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseTo> getAuthorById(@PathVariable String id) {
        try {
            Long authorId = Long.parseLong(id);
            AuthorResponseTo author = authorService.getById(authorId);
            return ResponseEntity.ok(author);
        } catch (NumberFormatException e) {
            return ResponseEntity.ok(null);
        }
    }

    @PostMapping
    public ResponseEntity<AuthorResponseTo> createAuthor(@Valid @RequestBody AuthorRequestTo request) {
        AuthorResponseTo created = authorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseTo> updateAuthor(@PathVariable String id, @Valid @RequestBody AuthorRequestTo request) {
        try {
            Long authorId = Long.parseLong(id);
            AuthorResponseTo updated = authorService.update(authorId, request);
            return ResponseEntity.ok(updated);
        } catch (NumberFormatException e) {
            return ResponseEntity.ok(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable String id) {
        try {
            Long authorId = Long.parseLong(id);
            authorService.delete(authorId);
            return ResponseEntity.noContent().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.noContent().build();
        }
    }
}