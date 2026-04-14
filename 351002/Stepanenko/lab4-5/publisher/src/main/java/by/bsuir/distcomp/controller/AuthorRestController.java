package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.AuthorRequestTo;
import by.bsuir.distcomp.dto.response.AuthorResponseTo;
import by.bsuir.distcomp.core.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/authors")
public class AuthorRestController {
    private final AuthorService authorService;

    public AuthorRestController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<AuthorResponseTo> create(@Valid @RequestBody AuthorRequestTo request) {
        AuthorResponseTo createdAuthor = authorService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdAuthor);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseTo> getById(@PathVariable("id") Long authorId) {
        AuthorResponseTo author = authorService.getById(authorId);
        return ResponseEntity
                .ok(author);
    }

    @GetMapping
    public ResponseEntity<List<AuthorResponseTo>> getAll() {
        List<AuthorResponseTo> authors = authorService.getAll();
        return ResponseEntity
                .ok(authors);
    }

    @PutMapping
    public ResponseEntity<AuthorResponseTo> update(@Valid @RequestBody AuthorRequestTo request) {
        AuthorResponseTo updatedAuthor = authorService.update(request);
        return ResponseEntity
                .ok(updatedAuthor);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long authorId) {
        authorService.deleteById(authorId);
        return ResponseEntity
                .noContent()
                .build();
    }
}
