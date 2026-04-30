package com.example.kafkademo.controller.v1;

import com.example.kafkademo.entity.Tweet;
import com.example.kafkademo.service.TweetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetControllerV1 {

    @Autowired
    private TweetService tweetService;

    @GetMapping
    public List<Tweet> getAll() {
        return tweetService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tweet> getById(@PathVariable Long id) {
        return tweetService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-creator/{creatorId}")
    public List<Tweet> getByCreatorId(@PathVariable Long creatorId) {
        return tweetService.findByCreatorId(creatorId);
    }

    @PostMapping
    public ResponseEntity<Tweet> create(@RequestBody Tweet tweet) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tweetService.save(tweet));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tweet> update(@PathVariable Long id, @RequestBody Tweet tweet) {
        if (!tweetService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        tweet.setId(id);
        return ResponseEntity.ok(tweetService.save(tweet));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!tweetService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        tweetService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}