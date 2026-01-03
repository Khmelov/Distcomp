package com.task.rest.controller;

import com.task.rest.dto.MarkRequestTo;
import com.task.rest.dto.MarkResponseTo;
import com.task.rest.service.MarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/marks")
@RequiredArgsConstructor
public class MarkController {

    private final MarkService service;

    @PostMapping
    public ResponseEntity<MarkResponseTo> create(@Valid @RequestBody MarkRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<MarkResponseTo>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping
    public ResponseEntity<MarkResponseTo> update(@Valid @RequestBody MarkRequestTo request) {
        return ResponseEntity.ok(service.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<MarkResponseTo>> getByTweetId(@PathVariable Long tweetId) {
        return ResponseEntity.ok(service.getByTweetId(tweetId));
    }
}