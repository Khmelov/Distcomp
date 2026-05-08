package com.example.demo.controller;

import com.example.demo.entity.Author;
import com.example.demo.service.AuthorService;
import com.example.demo.specification.AuthorSpecification;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/authors")
@Profile("docker")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @PostMapping
    public ResponseEntity<Author> create(@Valid @RequestBody Author author) {
        return new ResponseEntity<>(authorService.create(author), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Author> update(@PathVariable Long id, @Valid @RequestBody Author author) {
        author.setId(id);
        return ResponseEntity.ok(authorService.update(author));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Author> findById(@PathVariable Long id) {
        return authorService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Плоский список (без пагинации) – для тестов
    @GetMapping
    public ResponseEntity<List<Author>> findAllList(
            @RequestParam(required = false) String login,
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Sort sorting = Sort.by(direction, sort[0]);

        Specification<Author> spec = Specification.where(AuthorSpecification.loginEquals(login))
                .and(AuthorSpecification.firstnameContains(firstname))
                .and(AuthorSpecification.lastnameContains(lastname));

        List<Author> authors = authorService.findAll(spec, sorting);
        return ResponseEntity.ok(authors);
    }

    // Пагинация – отдельный endpoint (по требованию задания)
    @GetMapping("/page")
    public ResponseEntity<Page<Author>> findAllPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(required = false) String login,
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname) {

        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Sort sorting = Sort.by(direction, sort[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);

        Specification<Author> spec = Specification.where(AuthorSpecification.loginEquals(login))
                .and(AuthorSpecification.firstnameContains(firstname))
                .and(AuthorSpecification.lastnameContains(lastname));

        return ResponseEntity.ok(authorService.findAll(spec, pageable));
    }
}