package org.polozkov.controller.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.comment.CommentRequestTo;
import org.polozkov.dto.comment.CommentResponseTo;
import org.polozkov.service.comment.CommentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentResponseTo> getAllComments() {
        return commentService.getAllComments();
    }

    @GetMapping("/{id}")
    public CommentResponseTo getCommentById(@PathVariable Long id) {
        return commentService.getCommentById(id);
    }

    @PostMapping
    public CommentResponseTo createComment(@Valid @RequestBody CommentRequestTo commentRequest) {
        return commentService.createComment(commentRequest);
    }

    @PutMapping
    public CommentResponseTo updateComment(@Valid @RequestBody CommentRequestTo commentRequest) {
        return commentService.updateComment(commentRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}