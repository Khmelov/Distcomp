package com.restApp.controller;

import com.restApp.client.DiscussionClient;
import com.restApp.dto.CommentRequestTo;
import com.restApp.dto.CommentResponseTo;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/comments")
public class CommentController {

    private final DiscussionClient discussionClient;
    private final com.restApp.service.CommentProducerService commentProducerService;

    public CommentController(DiscussionClient discussionClient,
            com.restApp.service.CommentProducerService commentProducerService) {
        this.discussionClient = discussionClient;
        this.commentProducerService = commentProducerService;
    }

    @PostMapping
    public ResponseEntity<CommentResponseTo> create(@RequestBody @Valid CommentRequestTo request) {
        if (request.getId() == null) {
            request.setId(java.util.concurrent.ThreadLocalRandom.current().nextLong(Long.MAX_VALUE));
        }
        CommentResponseTo createdResponse = discussionClient.create(request);
        return new ResponseEntity<>(createdResponse, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseTo>> getAll() {
        return ResponseEntity.ok(discussionClient.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseTo> getById(@PathVariable Long id) {
        CommentResponseTo response = discussionClient.findById(id);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseTo> update(@PathVariable Long id,
            @RequestBody @Valid CommentRequestTo request) {
        CommentResponseTo response = discussionClient.update(id, request);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        discussionClient.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/news/{newsId}")
    public ResponseEntity<List<CommentResponseTo>> getByNewsId(@PathVariable Long newsId) {
        return ResponseEntity.ok(discussionClient.getCommentsByNewsId(newsId));
    }
}
