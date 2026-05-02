package com.example.demo.cassandra.controller;

import com.example.demo.cassandra.model.CommentCassandra;
import com.example.demo.cassandra.service.CommentCassandraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.context.annotation.Profile;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/comments")
@Profile("cassandra")
public class CommentCassandraController {
    @Autowired
    private CommentCassandraService service;

    @PostMapping
    public ResponseEntity<CommentCassandra> create(@RequestBody CommentCassandra comment) {
        return new ResponseEntity<>(service.save(comment), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentCassandra> update(@PathVariable Long id, @RequestBody CommentCassandra comment) {
        comment.setId(id);
        return ResponseEntity.ok(service.save(comment));
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
    public ResponseEntity<List<CommentCassandra>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}