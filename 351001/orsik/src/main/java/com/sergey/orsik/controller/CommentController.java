package com.sergey.orsik.controller;

import com.sergey.orsik.dto.request.CommentRequestTo;
import com.sergey.orsik.dto.response.CommentResponseTo;
import com.sergey.orsik.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentResponseTo> findAll() {
        return commentService.findAll();
    }

    @GetMapping("/{id}")
    public CommentResponseTo findById(@PathVariable Long id) {
        return commentService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseTo create(@Valid @RequestBody CommentRequestTo request) {
        return commentService.create(request);
    }

    @PutMapping
    public CommentResponseTo updateByBody(@Valid @RequestBody CommentRequestTo request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("id is required for update");
        }
        return commentService.update(request.getId(), request);
    }

    @PutMapping("/{id}")
    public CommentResponseTo update(@PathVariable Long id, @Valid @RequestBody CommentRequestTo request) {
        return commentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        commentService.deleteById(id);
    }
}
