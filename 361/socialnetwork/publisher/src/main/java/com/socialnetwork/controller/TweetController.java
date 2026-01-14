package com.socialnetwork.controller;

import com.socialnetwork.dto.request.TweetRequestTo;
import com.socialnetwork.dto.response.TweetResponseTo;
import com.socialnetwork.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetController {

    @Autowired
    private TweetService tweetService;

    @GetMapping
    public ResponseEntity<List<TweetResponseTo>> getAllTweets() {
        List<TweetResponseTo> tweets = tweetService.getAll();
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TweetResponseTo> getTweetById(@PathVariable Long id) {
        TweetResponseTo tweet = tweetService.getById(id);
        return ResponseEntity.ok(tweet);
    }

    @PostMapping
    public ResponseEntity<TweetResponseTo> createTweet(@Valid @RequestBody TweetRequestTo request) {
        TweetResponseTo createdTweet = tweetService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTweet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TweetResponseTo> updateTweet(@PathVariable Long id,
                                                       @Valid @RequestBody TweetRequestTo request) {
        TweetResponseTo updatedTweet = tweetService.update(id, request);
        return ResponseEntity.ok(updatedTweet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable Long id) {
        tweetService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/page")
    public ResponseEntity<Page<TweetResponseTo>> getTweetsPage(
            @PageableDefault(size = 10, sort = "created", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TweetResponseTo> tweets = tweetService.getAll(pageable);
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TweetResponseTo>> getTweetsByUserId(@PathVariable Long userId) {
        List<TweetResponseTo> tweets = tweetService.getByUserId(userId);
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/user/{userId}/page")
    public ResponseEntity<Page<TweetResponseTo>> getTweetsByUserIdPage(
            @PathVariable Long userId,
            @PageableDefault(size = 10, sort = "created", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TweetResponseTo> tweets = tweetService.getByUserId(userId, pageable);
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/label/{labelId}")
    public ResponseEntity<List<TweetResponseTo>> getTweetsByLabelId(@PathVariable Long labelId) {
        List<TweetResponseTo> tweets = tweetService.getByLabelId(labelId);
        return ResponseEntity.ok(tweets);
    }

    @GetMapping("/label/{labelId}/page")
    public ResponseEntity<Page<TweetResponseTo>> getTweetsByLabelIdPage(
            @PathVariable Long labelId,
            @PageableDefault(size = 10, sort = "created", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TweetResponseTo> tweets = tweetService.getByLabelId(labelId, pageable);
        return ResponseEntity.ok(tweets);
    }
}