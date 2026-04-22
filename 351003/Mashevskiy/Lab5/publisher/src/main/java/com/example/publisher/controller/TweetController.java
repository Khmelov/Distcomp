package com.example.publisher.controller;

import com.example.publisher.entity.Tweet;
import com.example.publisher.repository.TweetRepository;
import com.example.publisher.repository.CreatorRepository;
import com.example.publisher.service.CacheService;
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

    @Autowired
    private CacheService cacheService;

    private static final String CACHE_KEY_PREFIX = "tweet:";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tweet createTweet(@RequestBody Tweet tweet) {
        if (!creatorRepository.existsById(tweet.getCreatorId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Creator not found");
        }
        Tweet saved = tweetRepository.save(tweet);
        cacheService.put(CACHE_KEY_PREFIX + saved.getId(), saved);
        return saved;
    }

    @GetMapping
    public Iterable<Tweet> getAllTweets() {
        return tweetRepository.findAll();
    }

    @GetMapping("/{id}")
    public Tweet getTweet(@PathVariable Long id) {
        String cacheKey = CACHE_KEY_PREFIX + id;
        Tweet cached = (Tweet) cacheService.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        Tweet tweet = tweetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        cacheService.put(cacheKey, tweet);
        return tweet;
    }

    @PutMapping("/{id}")
    public Tweet updateTweet(@PathVariable Long id, @RequestBody Tweet tweet) {
        if (!tweetRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        tweet.setId(id);
        Tweet updated = tweetRepository.save(tweet);
        cacheService.put(CACHE_KEY_PREFIX + id, updated);
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTweet(@PathVariable Long id) {
        tweetRepository.deleteById(id);
        cacheService.evict(CACHE_KEY_PREFIX + id);
    }
}