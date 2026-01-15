package by.bsuir.entitiesapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import by.bsuir.entitiesapp.dto.AuthorRequestTo;
import by.bsuir.entitiesapp.dto.AuthorResponseTo;
import by.bsuir.entitiesapp.service.AuthorService;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/authors")
public class AuthorController {

    private final AuthorService service;

    public AuthorController(AuthorService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponseTo create(@RequestBody AuthorRequestTo dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<AuthorResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public AuthorResponseTo get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public AuthorResponseTo update(@PathVariable Long id, @RequestBody AuthorRequestTo dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
