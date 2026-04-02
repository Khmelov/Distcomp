package org.discussion.controller.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.discussion.dto.comment.CommentRequestTo;
import org.discussion.dto.comment.CommentResponseTo;
import org.discussion.repository.comment.CommentRepository;
import org.discussion.service.comment.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentRepository repository;

    // Создание комментария
    private final CommentService commentService;

    @GetMapping
    public List<CommentResponseTo> getAllComments() {
        return commentService.getAllComments();
    }

    @GetMapping("/{id}")
    public CommentResponseTo getComment(@PathVariable Long id) {
        return commentService.getComment(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseTo createComment(@Valid @RequestBody CommentRequestTo commentRequest) {
        return commentService.createComment(commentRequest);
    }

    @PutMapping
    public CommentResponseTo updateComment(@Valid @RequestBody CommentRequestTo commentRequest) {
        return commentService.updateComment(commentRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}