package by.rest.publisher.controller;

import by.rest.publisher.dto.comment.CommentRequestTo;
import by.rest.publisher.dto.comment.CommentResponseTo;
import by.rest.publisher.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

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
                    .created(URI.create("/api/v1.0/comments/" + response.getId()))
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseTo> getComment(@PathVariable UUID id) {
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
    public ResponseEntity<Void> deleteComment(@PathVariable UUID id) {
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
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CommentResponseTo>> getCommentsByStatus(@PathVariable String status) {
        try {
            List<CommentResponseTo> comments = commentService.getCommentsByStatus(status);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Comment Controller is working!");
    }
}