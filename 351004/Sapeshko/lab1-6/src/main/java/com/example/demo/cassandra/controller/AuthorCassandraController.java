package com.example.demo.cassandra.controller;

import com.example.demo.cassandra.model.AuthorCassandra;
import com.example.demo.cassandra.service.AuthorCassandraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.context.annotation.Profile;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/authors")
@Profile("cassandra")
public class AuthorCassandraController {
    @Autowired
    private AuthorCassandraService service;

    @PostMapping
    public ResponseEntity<AuthorCassandra> create(@RequestBody AuthorCassandra author) {
        return new ResponseEntity<>(service.save(author), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorCassandra> update(@PathVariable Long id, @RequestBody AuthorCassandra author) {
        author.setId(id);
        return ResponseEntity.ok(service.save(author));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        var entity = service.findById(id);
        if (entity.isPresent()) {
            return ResponseEntity.ok().body(entity.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Entity not found"));
        }
    }

    @GetMapping
    public ResponseEntity<List<AuthorCassandra>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}