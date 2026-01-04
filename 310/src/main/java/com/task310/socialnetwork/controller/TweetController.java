package com.task310.socialnetwork.controller;

import com.task310.socialnetwork.dto.request.TweetRequestTo;
import com.task310.socialnetwork.dto.response.TweetResponseTo;
import com.task310.socialnetwork.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    @ResponseStatus(HttpStatus.OK)
    public List<TweetResponseTo> getAllTweets() {
        return tweetService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TweetResponseTo> getTweetById(@PathVariable Long id) {
        TweetResponseTo tweet = tweetService.getById(id);
        return ResponseEntity.ok(tweet);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TweetResponseTo createTweet(@Valid @RequestBody TweetRequestTo request) {
        return tweetService.create(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TweetResponseTo> updateTweet(@PathVariable Long id,
                                                       @Valid @RequestBody TweetRequestTo request) {
        TweetResponseTo updatedTweet = tweetService.update(id, request);
        return ResponseEntity.ok(updatedTweet);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTweet(@PathVariable Long id) {
        tweetService.delete(id);
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TweetResponseTo> getTweetsByUserId(@PathVariable Long userId) {
        return tweetService.getByUserId(userId);
    }

    @GetMapping("/label/{labelId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TweetResponseTo> getTweetsByLabelId(@PathVariable Long labelId) {
        return tweetService.getByLabelId(labelId);
    }
}