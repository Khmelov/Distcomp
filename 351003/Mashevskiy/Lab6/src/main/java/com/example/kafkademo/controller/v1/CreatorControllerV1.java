package com.example.kafkademo.controller.v1;

import com.example.kafkademo.entity.Creator;
import com.example.kafkademo.service.CreatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/creators")
public class CreatorControllerV1 {

    @Autowired
    private CreatorService creatorService;

    @GetMapping
    public List<Creator> getAll() {
        return creatorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Creator> getById(@PathVariable Long id) {
        return creatorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Creator> create(@RequestBody Creator creator) {
        return ResponseEntity.status(HttpStatus.CREATED).body(creatorService.save(creator));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Creator> update(@PathVariable Long id, @RequestBody Creator creator) {
        if (!creatorService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        creator.setId(id);
        return ResponseEntity.ok(creatorService.save(creator));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        creatorService.deleteById(id); // Просто удаляем, если есть
        return ResponseEntity.noContent().build(); // Всегда 204
    }
}