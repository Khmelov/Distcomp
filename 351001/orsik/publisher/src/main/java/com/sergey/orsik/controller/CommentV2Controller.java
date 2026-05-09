package com.sergey.orsik.controller;

import com.sergey.orsik.dto.request.CommentRequestTo;
import com.sergey.orsik.dto.response.CommentResponseTo;
import com.sergey.orsik.service.CommentService;
import com.sergey.orsik.service.SecuredResourceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/comments")
public class CommentV2Controller {

    private final CommentService commentService;
    private final SecuredResourceService securedResourceService;

    public CommentV2Controller(CommentService commentService, SecuredResourceService securedResourceService) {
        this.commentService = commentService;
        this.securedResourceService = securedResourceService;
    }

    @GetMapping
    public List<CommentResponseTo> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Long tweetId,
            @RequestParam(required = false) String content) {
        return commentService.findAll(page, size, sortBy, sortDir, tweetId, content);
    }

    @GetMapping("/{id}")
    public CommentResponseTo findById(@PathVariable Long id) {
        return commentService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseTo create(@Valid @RequestBody CommentRequestTo request) {
        return securedResourceService.createComment(request);
    }

    @PutMapping
    public CommentResponseTo updateByBody(@Valid @RequestBody CommentRequestTo request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("id is required for update");
        }
        return securedResourceService.updateComment(request.getId(), request);
    }

    @PutMapping("/{id}")
    public CommentResponseTo update(@PathVariable Long id, @Valid @RequestBody CommentRequestTo request) {
        return securedResourceService.updateComment(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        securedResourceService.deleteComment(id);
    }
}
