package com.task.rest.Controller;

import com.task.rest.dto.CommentRequestTo;
import com.task.rest.dto.CommentResponseTo;
import com.task.rest.service.CommentService;
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
    public ResponseEntity<CommentResponseTo> createComment(@Valid @RequestBody CommentRequestTo requestTo) {
        CommentResponseTo response = commentService.createComment(requestTo);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseTo> getCommentById(@PathVariable Long id) {
        CommentResponseTo response = commentService.getCommentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseTo>> getAllComments() {
        List<CommentResponseTo> comments = commentService.getAllComments();
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseTo> updateComment(@PathVariable Long id, @Valid @RequestBody CommentRequestTo requestTo) {
        CommentResponseTo response = commentService.updateComment(id, requestTo);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-tweet/{tweetId}")
    public ResponseEntity<List<CommentResponseTo>> getCommentsByTweetId(@PathVariable Long tweetId) {
        List<CommentResponseTo> comments = commentService.getCommentsByTweetId(tweetId);
        return ResponseEntity.ok(comments);
    }
}
