package com.example.rest.controller;

import com.example.rest.dto.CreatorRequestTo;
import com.example.rest.dto.CreatorResponseTo;
import com.example.rest.dto.CreatorUpdate;
import com.example.rest.service.CreatorNotFoundException;
import com.example.rest.service.CreatorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1.0/creators")
public class CreatorController {

    private final CreatorService creatorService;

    @Autowired
    public CreatorController(CreatorService creatorService) {
        this.creatorService = creatorService;
    }

    @GetMapping
    public ResponseEntity<List<CreatorResponseTo>> findAll() {
        return ResponseEntity.ok(creatorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreatorResponseTo> findById(@PathVariable Long id) {
        Optional<CreatorResponseTo> creator = creatorService.findById(id);
        return creator.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CreatorResponseTo> create(@Valid @RequestBody CreatorRequestTo creatorRequestTo) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(creatorService.create(creatorRequestTo));

    }

    @PutMapping()
    public ResponseEntity<CreatorResponseTo> update(@Valid @RequestBody CreatorUpdate creatorUpdate) {
        return ResponseEntity.ok(creatorService.update(creatorUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        creatorService.findById(id).orElseThrow(() -> new CreatorNotFoundException(id));
        creatorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
