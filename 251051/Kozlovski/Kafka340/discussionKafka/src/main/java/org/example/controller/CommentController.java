package org.example.controller;

import org.example.dto.CommentRequestTo;
import org.example.dto.CommentResponseTo;
import org.example.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/comments")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CommentResponseTo> create(@Valid @RequestBody CommentRequestTo dto) {
        CommentResponseTo created = service.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{country}/{tweetId}")
    public ResponseEntity<List<CommentResponseTo>> getByTweet(
            @PathVariable String country,
            @PathVariable Long tweetId) {
        return ResponseEntity.ok(service.getByTweet(country, tweetId));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseTo>> getAllComments() {
        return ResponseEntity.ok(service.getAllComments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseTo> getById(@PathVariable Long id) {
        CommentResponseTo resp = service.getById(id);
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseTo> update(
            @PathVariable Long id,
            @RequestBody @Valid CommentRequestTo dto
    ) {
        CommentResponseTo updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}