package com.example.demo.controllers.v1;

import com.example.demo.dto.requests.AuthorRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.service.AuthorService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1.0/authors")
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    //CREATE - POST /authors
    @PostMapping
    public ResponseEntity<AuthorResponseTo> create(@Valid @RequestBody AuthorRequestTo dto){
        AuthorResponseTo response = authorService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //READ ALL - GET /authors
    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id,asc") String sort,
            @RequestParam(name = "login", required = false) String login,
            @RequestParam(name = "firstname", required = false) String firstname,
            @RequestParam(name = "lastname", required = false) String lastname) {

        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, parseSort(sort));
            return ResponseEntity.ok(authorService.findAll(pageable, login, firstname, lastname));
        } else {
            return ResponseEntity.ok(authorService.findAll(login, firstname, lastname));
        }
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }

    //READ BY ID - GET /authors/1
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseTo> findById(@PathVariable("id") Long id) {
        AuthorResponseTo author = authorService.findById(id);
        return ResponseEntity.ok(author);
    }

    // UPDATE - PUT /authors/1
    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseTo> update(@PathVariable("id") Long id,
                                                   @Valid
                                                   @RequestBody AuthorRequestTo dto) {
        AuthorResponseTo updated = authorService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    //DELETE - DELETE /authors/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
