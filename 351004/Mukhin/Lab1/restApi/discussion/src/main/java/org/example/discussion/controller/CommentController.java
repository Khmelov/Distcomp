package org.example.discussion.controller;

import org.example.discussion.dto.CommentRequestTo;
import org.example.discussion.dto.CommentResponseTo;
import org.example.discussion.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseTo>> getAll() {
        return ResponseEntity.ok(commentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getById(id));
    }

    @PostMapping
    public ResponseEntity<CommentResponseTo> create(@Valid @RequestBody CommentRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseTo> update(@PathVariable Long id,
                                                    @Valid @RequestBody CommentRequestTo request) {
        return ResponseEntity.ok(commentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}