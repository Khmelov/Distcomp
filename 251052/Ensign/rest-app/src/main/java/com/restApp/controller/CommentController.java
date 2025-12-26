package com.restApp.controller;

import com.restApp.dto.CommentRequestTo;
import com.restApp.dto.CommentResponseTo;
import com.restApp.service.CommentService;
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
    public ResponseEntity<CommentResponseTo> create(@RequestBody CommentRequestTo request) {
        return new ResponseEntity<>(commentService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseTo>> getAll() {
        return ResponseEntity.ok(commentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseTo> update(@PathVariable Long id, @RequestBody CommentRequestTo request) {
        return ResponseEntity.ok(commentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        commentService.delete(id);
    }
}
