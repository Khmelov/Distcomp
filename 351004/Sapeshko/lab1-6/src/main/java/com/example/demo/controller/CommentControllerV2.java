package com.example.demo.controller;

import com.example.demo.dto.CommentRequest;
import com.example.demo.entity.Comment;
import com.example.demo.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;
import java.util.List;

@RestController
@RequestMapping("/api/v2.0/comments")
@Profile("docker")
public class CommentControllerV2 {

    private final CommentService commentService;

    public CommentControllerV2(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getAll() {
        return ResponseEntity.ok(commentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comment> getById(@PathVariable Long id) {
        return commentService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new com.example.demo.exception.NotFoundException("Comment not found"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<Comment> create(@RequestBody CommentRequest request) {
        Comment created = commentService.createFromRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @ownershipValidator.isCommentOwner(#id, authentication.name))")
    public ResponseEntity<Comment> update(@PathVariable Long id, @RequestBody CommentRequest request) {
        return ResponseEntity.ok(commentService.updateFromRequest(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @ownershipValidator.isCommentOwner(#id, authentication.name))")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}