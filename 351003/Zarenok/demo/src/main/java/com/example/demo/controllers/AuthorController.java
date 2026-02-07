package com.example.demo.controllers;

import com.example.demo.dto.requests.AuthorRequestTo;
import com.example.demo.dto.responses.AuthorResponseTo;
import com.example.demo.service.AuthorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/authors")
@Validated
public class AuthorController {
    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    //CREATE - POST /authors
    @PostMapping
    public ResponseEntity<AuthorResponseTo> create( @RequestBody AuthorRequestTo dto){
        AuthorResponseTo response = authorService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //READ ALL - GET /authors
    @GetMapping
    public ResponseEntity<List<AuthorResponseTo>> findAll(){
        List<AuthorResponseTo> list = authorService.findAll();
        return ResponseEntity.ok(list);
    }

    //READ BY ID - GET /authors/1
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseTo> findById(@PathVariable Long id){
        AuthorResponseTo author = authorService.findById(id);
        return ResponseEntity.ok(author);
    }

    // UPDATE - PUT /authors/1
    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseTo> update(@PathVariable Long id,
                                                   @RequestBody AuthorRequestTo dto){
        AuthorResponseTo updated = authorService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    //DELETE - DELETE /authors/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
