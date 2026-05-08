package com.example.publisher.controller;

import com.example.publisher.entity.Tweet;
import com.example.publisher.repository.TweetRepository;
import com.example.publisher.repository.CreatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetController {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private CreatorRepository creatorRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tweet createTweet(@RequestBody Tweet tweet) {
        if (tweet.getCreatorId() == null || !creatorRepository.existsById(tweet.getCreatorId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Creator not found");
        }
        return tweetRepository.save(tweet);
    }

    @GetMapping
    public Iterable<Tweet> getAllTweets() {
        return tweetRepository.findAll();
    }

    @GetMapping("/{id}")
    public Tweet getTweet(@PathVariable Long id) {
        return tweetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public Tweet updateTweet(@PathVariable Long id, @RequestBody Tweet tweet) {
        if (!tweetRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        tweet.setId(id);
        return tweetRepository.save(tweet);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTweet(@PathVariable Long id) {
        tweetRepository.deleteById(id);
    }
}