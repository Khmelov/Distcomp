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
@RequestMapping(value = "/api/v1.0/authors")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping
    ResponseEntity<List<AuthorResponseTo>> getAllAuthors() {
        return new ResponseEntity<>(authorService.getAllAuthors(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id:\\d+}")
    ResponseEntity<AuthorResponseTo> getAuthorById(@PathVariable Long id) {
        return new ResponseEntity<>(authorService.getAuthorById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AuthorResponseTo> createAuthor(@RequestBody @Valid AuthorRequestTo authorRequestTo) {
        return new ResponseEntity<>(authorService.createAuthor(authorRequestTo), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<AuthorResponseTo> updateAuthor(@RequestBody @Valid AuthorRequestTo authorRequestTo) {
        return new ResponseEntity<>(authorService.updateAuthor(authorRequestTo), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id:\\d+}")
    public ResponseEntity<AuthorResponseTo> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
