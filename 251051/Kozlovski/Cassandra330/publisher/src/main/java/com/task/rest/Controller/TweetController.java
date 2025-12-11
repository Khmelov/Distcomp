package com.task.rest.Controller;

import com.task.rest.dto.TweetRequestTo;
import com.task.rest.dto.TweetResponseTo;
import com.task.rest.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetController {
    private final TweetService tweetService;

    public TweetController(TweetService tweetService) {
        this.tweetService = tweetService;
    }

    @PostMapping
    public ResponseEntity<TweetResponseTo> createTweet(@Valid @RequestBody TweetRequestTo requestTo) {
        TweetResponseTo response = tweetService.createTweet(requestTo);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TweetResponseTo> getTweetById(@PathVariable Long id) {
        TweetResponseTo response = tweetService.getTweetById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TweetResponseTo>> getAllTweets() {
        List<TweetResponseTo> tweets = tweetService.getAllTweets();
        return ResponseEntity.ok(tweets);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TweetResponseTo> updateTweet(@PathVariable Long id, @Valid @RequestBody TweetRequestTo requestTo) {
        TweetResponseTo response = tweetService.updateTweet(id, requestTo);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable Long id) {
        tweetService.deleteTweet(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-mark-id/{markId}")
    public ResponseEntity<List<TweetResponseTo>> getTweetsByMarkId(@PathVariable Long markId) {
        List<TweetResponseTo> tweets = tweetService.getTweetsByMarkId(markId);
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/by-writer-id/{writerId}")
    public ResponseEntity<List<TweetResponseTo>> getTweetsByWriterId(@PathVariable Long writerId) {
        List<TweetResponseTo> tweets = tweetService.getTweetsByWriterId(writerId);
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/by-mark-name/{markName}")
    public ResponseEntity<List<TweetResponseTo>> getTweetsByMarkName(@PathVariable String markName) {
        List<TweetResponseTo> tweets = tweetService.getTweetsByMarkName(markName);
        return ResponseEntity.ok(tweets);
    }
}
