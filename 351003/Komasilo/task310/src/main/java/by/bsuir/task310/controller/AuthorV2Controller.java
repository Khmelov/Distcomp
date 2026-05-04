package by.bsuir.task310.controller;

import by.bsuir.task310.dto.AuthorRequestTo;
import by.bsuir.task310.dto.AuthorResponseTo;
import by.bsuir.task310.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/authors")
public class AuthorV2Controller {

    private final AuthorService service;

    public AuthorV2Controller(AuthorService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponseTo create(@Valid @RequestBody AuthorRequestTo requestTo) {
        return service.create(requestTo);
    }

    @GetMapping
    public List<AuthorResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public AuthorResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping
    public AuthorResponseTo update(@Valid @RequestBody AuthorRequestTo requestTo) {
        return service.update(requestTo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}