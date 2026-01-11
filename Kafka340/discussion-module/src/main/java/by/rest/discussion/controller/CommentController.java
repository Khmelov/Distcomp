// src/main/java/by/rest/discussion/controller/CommentController.java
package by.rest.discussion.controller;

import by.rest.discussion.dto.CommentRequestTo;
import by.rest.discussion.dto.CommentResponseTo;
import by.rest.discussion.service.CommentService;
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
    public ResponseEntity<CommentResponseTo> createComment(@Valid @RequestBody CommentRequestTo request) {
        try {
            CommentResponseTo response = commentService.createComment(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseTo> getComment(@PathVariable Long id) {
        try {
            CommentResponseTo response = commentService.getCommentById(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/story/{storyId}")
    public ResponseEntity<List<CommentResponseTo>> getCommentsByStory(@PathVariable Long storyId) {
        try {
            List<CommentResponseTo> comments = commentService.getCommentsByStoryId(storyId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        try {
            commentService.deleteComment(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<CommentResponseTo>> getAllComments() {
        try {
            List<CommentResponseTo> comments = commentService.getAllComments();
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Discussion Comment Controller is working!");
    }
}