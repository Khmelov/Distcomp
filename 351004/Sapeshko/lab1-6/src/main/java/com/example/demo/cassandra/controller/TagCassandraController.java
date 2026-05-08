package com.example.demo.cassandra.controller;

import com.example.demo.cassandra.model.TagCassandra;
import com.example.demo.cassandra.service.TagCassandraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.context.annotation.Profile;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/tags")
@Profile("cassandra")
public class TagCassandraController {
    @Autowired
    private TagCassandraService service;

    @PostMapping
    public ResponseEntity<TagCassandra> create(@RequestBody TagCassandra tag) {
        return new ResponseEntity<>(service.save(tag), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagCassandra> update(@PathVariable Long id, @RequestBody TagCassandra tag) {
        tag.setId(id);
        return ResponseEntity.ok(service.save(tag));
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
    public ResponseEntity<List<TagCassandra>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}