package com.example.task310.controller;

import com.example.task310.dto.CreatorRequestTo;
import com.example.task310.dto.CreatorResponseTo;
import com.example.task310.service.CreatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/creators")
@RequiredArgsConstructor
public class CreatorController {
    private final CreatorService creatorService;

    @PostMapping
    public ResponseEntity<CreatorResponseTo> create(@Valid @RequestBody CreatorRequestTo request) {
        CreatorResponseTo response = creatorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CreatorResponseTo>> findAll() {
        return ResponseEntity.ok(creatorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreatorResponseTo> findById(@PathVariable Long id) {
        return ResponseEntity.ok(creatorService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CreatorResponseTo> update(@PathVariable Long id,
                                                    @Valid @RequestBody CreatorRequestTo request) {
        return ResponseEntity.ok(creatorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        creatorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}