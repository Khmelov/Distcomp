package com.sergey.orsik.discussion.controller;

import com.sergey.orsik.discussion.service.CommentDiscussionService;
import com.sergey.orsik.dto.request.CommentRequestTo;
import com.sergey.orsik.dto.response.CommentResponseTo;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/comments")
public class CommentController {

    private final CommentDiscussionService commentDiscussionService;

    public CommentController(CommentDiscussionService commentDiscussionService) {
        this.commentDiscussionService = commentDiscussionService;
    }

    @GetMapping
    public List<CommentResponseTo> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Long tweetId,
            @RequestParam(required = false) String content) {
        return commentDiscussionService.findAll(page, size, sortBy, sortDir, tweetId, content);
    }

    @GetMapping("/{id}")
    public CommentResponseTo findById(@PathVariable Long id) {
        return commentDiscussionService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseTo create(@Valid @RequestBody CommentRequestTo request) {
        return commentDiscussionService.create(request);
    }

    @PutMapping
    public CommentResponseTo updateByBody(@Valid @RequestBody CommentRequestTo request) {
        if (request.getId() == null) {
            throw new IllegalArgumentException("id is required for update");
        }
        return commentDiscussionService.update(request.getId(), request);
    }

    @PutMapping("/{id}")
    public CommentResponseTo update(@PathVariable Long id, @Valid @RequestBody CommentRequestTo request) {
        return commentDiscussionService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        commentDiscussionService.deleteById(id);
    }

    @DeleteMapping("/by-tweet/{tweetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByTweet(@PathVariable Long tweetId) {
        commentDiscussionService.deleteAllByTweetId(tweetId);
    }
}
