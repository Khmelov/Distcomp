package com.example.demo.controller;

import com.example.demo.entity.Author;
import com.example.demo.exception.NotFoundException;
import com.example.demo.service.AuthorService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/authors")
@Profile("!cassandra")
public class AuthorControllerV2 {

    private final AuthorService authorService;

    public AuthorControllerV2(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    public ResponseEntity<List<Author>> getAll() {
        return ResponseEntity.ok(authorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Author> getById(@PathVariable Long id) {
        return authorService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Author not found with id: " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @ownershipValidator.isAuthorOwner(#id, authentication.name))")
    public ResponseEntity<Author> update(@PathVariable Long id, @RequestBody Author author) {
        author.setId(id);
        Author updated = authorService.update(author);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @ownershipValidator.isAuthorOwner(#id, authentication.name))")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}