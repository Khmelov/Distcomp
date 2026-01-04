package com.example.discussion.controller;

import com.example.discussion.dto.CommentRequestTo;
import com.example.discussion.dto.CommentResponseTo;
import com.example.discussion.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/comments")
public class CommentController {
    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseTo>> getComments() {
        return ResponseEntity.ok(service.getAllComments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseTo> getComment(@PathVariable Long id) {
        return ResponseEntity.ok(service.getCommentById(id));
    }

    @GetMapping("/byStory/{storyId}")
    public ResponseEntity<List<CommentResponseTo>> getCommentsByStory(@PathVariable Long storyId) {
        return ResponseEntity.ok(service.getCommentsByStoryId(storyId));
    }

    @PostMapping
    public ResponseEntity<CommentResponseTo> createComment(@Valid @RequestBody CommentRequestTo request) {
        CommentResponseTo created = service.createComment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping
    public ResponseEntity<CommentResponseTo> updateComment(@Valid @RequestBody CommentRequestTo request) {
        return ResponseEntity.ok(service.updateComment(request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long id) {
        service.deleteComment(id);
    }
}