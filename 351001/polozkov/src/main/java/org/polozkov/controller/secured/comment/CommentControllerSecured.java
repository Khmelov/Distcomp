package org.polozkov.controller.secured.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.polozkov.dto.comment.CommentRequestTo;
import org.polozkov.dto.comment.CommentResponseTo;
import org.polozkov.service.comment.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/comments")
@RequiredArgsConstructor
public class CommentControllerSecured {

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
        // Здесь можно добавить проверку, что userId в запросе совпадает с текущим пользователем
        return commentService.createComment(commentRequest);
    }

    @PutMapping
    //@PreAuthorize("hasRole('ADMIN') or @commentSecurity.isOwner(#commentRequest.id)")
    public CommentResponseTo updateComment(@Valid @RequestBody CommentRequestTo commentRequest) {
        return commentService.updateComment(commentRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    //@PreAuthorize("hasRole('ADMIN') or @commentSecurity.isOwner(#id)")
    public void deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
    }
}