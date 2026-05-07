package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.AuthorRequestTo;
import by.bsuir.distcomp.dto.response.AuthorResponseTo;
import by.bsuir.distcomp.service.AuthorService;
import jakarta.validation.Valid;
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
    public ResponseEntity<AuthorResponseTo> create(@Valid @RequestBody AuthorRequestTo dto) {
        AuthorResponseTo response = authorService.create(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseTo> getById(@PathVariable Long id) {
        AuthorResponseTo response = authorService.getById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<AuthorResponseTo>> getAll() {
        List<AuthorResponseTo> response = authorService.getAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<AuthorResponseTo> update(@Valid @RequestBody AuthorRequestTo dto) {
        AuthorResponseTo response = authorService.update(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        authorService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
