package com.example.demo.controllers.v2;

import com.example.demo.dto.requests.AuthorRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.exception.NotFoundException;
import com.example.demo.service.AuthorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2.0/authors")
public class AuthorControllerV2 {

    private final AuthorService authorService;

    public AuthorControllerV2(AuthorService authorService) {
        this.authorService = authorService;
    }

    /*
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseTo> create(@Valid @RequestBody AuthorRequestTo dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.create(dto));
    }
    */

    @GetMapping
    @PreAuthorize("isAuthenticated()")
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

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @authorService.isOwner(#id, authentication.name))")
    public ResponseEntity<AuthorResponseTo> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(authorService.findById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @authorService.isOwner(#id, authentication.name))")
    public ResponseEntity<AuthorResponseTo> update(@PathVariable("id") Long id, @Valid @RequestBody AuthorRequestTo dto) {
        return ResponseEntity.ok(authorService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }

}