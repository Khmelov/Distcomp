package com.restApp.discussion.controller;

import com.restApp.discussion.dto.CommentRequestTo;
import com.restApp.discussion.dto.CommentResponseTo;
import com.restApp.discussion.service.CommentService;
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

    @PostMapping
    public ResponseEntity<CommentResponseTo> create(@RequestBody @Valid CommentRequestTo request) {
        return new ResponseEntity<>(commentService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseTo>> getAll() {
        return ResponseEntity.ok(commentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        CommentResponseTo response = commentService.findById(id);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return new ResponseEntity<>(
                new com.restApp.discussion.dto.ErrorResponse("Comment not found", "40401"),
                HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
            @RequestBody @Valid CommentRequestTo request) {
        CommentResponseTo response = commentService.update(id, request);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return new ResponseEntity<>(
                new com.restApp.discussion.dto.ErrorResponse("Comment not found for update", "40402"),
                HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/news/{newsId}")
    public ResponseEntity<List<CommentResponseTo>> getByNewsId(@PathVariable Long newsId) {
        return ResponseEntity.ok(commentService.getCommentsByNewsId(newsId));
    }

}
