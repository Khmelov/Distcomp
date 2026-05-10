package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.core.service.AuthorService;
import by.bsuir.distcomp.dto.request.AuthorRequestTo;
import by.bsuir.distcomp.dto.response.AuthorResponseTo;
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

    @GetMapping
    public List<AuthorResponseTo> getAll() {
        return authorService.getAll();
    }

    @GetMapping("/{id}")
    public AuthorResponseTo getById(@PathVariable Long id) {
        return authorService.getById(id);
    }

    @PostMapping
    public ResponseEntity<AuthorResponseTo> create(@Valid @RequestBody AuthorRequestTo request) {
        return new ResponseEntity<>(authorService.create(request), HttpStatus.CREATED);
    }

    @PutMapping
    public AuthorResponseTo update(@Valid @RequestBody AuthorRequestTo request) {
        return authorService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        authorService.deleteById(id);
    }
}