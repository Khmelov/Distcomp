package com.task.rest.Controller;

import com.task.rest.dto.CommentRequestTo;
import com.task.rest.dto.CommentResponseTo;
import com.task.rest.service.DiscussionClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/comments")
public class CommentGatewayController {

    private final DiscussionClient discussionClient;

    public CommentGatewayController(DiscussionClient discussionClient) {
        this.discussionClient = discussionClient;
    }

    @PostMapping
    public ResponseEntity<CommentResponseTo> create(@RequestBody CommentRequestTo dto) {
        CommentResponseTo created = discussionClient.createComment(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(discussionClient.getComment(id));
    }

    @GetMapping
    public ResponseEntity<List<CommentResponseTo>> getAllComments() {
        return ResponseEntity.ok(discussionClient.getAllComments());
    }

    @GetMapping("/by-tweet/{tweetId}")
    public ResponseEntity<List<CommentResponseTo>> getByTweetId(@PathVariable Long tweetId) {
        return ResponseEntity.ok(discussionClient.getCommentsByTweetId(tweetId));
    }

    @GetMapping("/{country}/{tweetId}")
    public ResponseEntity<List<CommentResponseTo>> getByCountryAndTweet(
            @PathVariable String country,
            @PathVariable Long tweetId) {
        return ResponseEntity.ok(discussionClient.getCommentsByCountryAndTweet(country, tweetId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponseTo> update(@PathVariable Long id, @RequestBody CommentRequestTo dto) {
        return ResponseEntity.ok(discussionClient.updateComment(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        discussionClient.deleteComment(id);
    }

}