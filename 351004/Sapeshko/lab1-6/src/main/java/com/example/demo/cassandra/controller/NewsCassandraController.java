package com.example.demo.cassandra.controller;

import com.example.demo.cassandra.model.NewsCassandra;
import com.example.demo.cassandra.service.NewsCassandraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.context.annotation.Profile;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/news")
@Profile("cassandra")
public class NewsCassandraController {
    @Autowired
    private NewsCassandraService service;

    @PostMapping
    public ResponseEntity<NewsCassandra> create(@RequestBody NewsCassandra news) {
        return new ResponseEntity<>(service.save(news), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsCassandra> update(@PathVariable Long id, @RequestBody NewsCassandra news) {
        news.setId(id);
        return ResponseEntity.ok(service.save(news));
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
    public ResponseEntity<List<NewsCassandra>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}