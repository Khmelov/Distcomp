package com.tweetservice.controllers;

import com.tweetservice.dtos.TweetRequestTo;
import com.tweetservice.dtos.TweetResponseTo;
import com.tweetservice.dtos.marker.MarkerResponseTo;
import com.tweetservice.dtos.message.MessageResponseTo;
import com.tweetservice.dtos.writer.WriterResponseTo;
import com.tweetservice.services.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0")
public class TweetController {

    private final TweetService tweetService;

    @PostMapping("/tweets")
    public ResponseEntity<TweetResponseTo> createTweet(@Valid @RequestBody TweetRequestTo request) {
        return new ResponseEntity<>(tweetService.createTweet(request), HttpStatus.CREATED);
    }

    @GetMapping("/tweets")
    public ResponseEntity<List<TweetResponseTo>> getAllTweets() {
        return ResponseEntity.ok(tweetService.findAllTweets());
    }

    @GetMapping("/tweets/{id}")
    public ResponseEntity<TweetResponseTo> getTweetById(@PathVariable Long id) {
        return ResponseEntity.ok(tweetService.findTweetById(id));
    }

    @PutMapping("/tweets/{id}")
    public ResponseEntity<TweetResponseTo> updateTweetById(@Valid @RequestBody TweetRequestTo request, @PathVariable Long id) {
        return ResponseEntity.ok(tweetService.updateTweetById(request, id));
    }

    @DeleteMapping("/tweets/{id}")
    public ResponseEntity<Void> deleteTweetById(@PathVariable Long id) {
        tweetService.deleteTweetById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tweets/by-writer/{writerId}")
    public ResponseEntity<Void> deleteTweetsByWriterId(@PathVariable Long writerId) {
        tweetService.deleteTweetsByWriterId(writerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tweets/{id}/writer")
    public ResponseEntity<WriterResponseTo> findWriterByTweetId(@PathVariable Long id) {
        return new ResponseEntity<>(tweetService.getWriterByTweetId(id), HttpStatus.OK);
    }

    @GetMapping("/tweets/{id}/message")
    public ResponseEntity<List<MessageResponseTo>> findAllMessagesByTweetId(@PathVariable Long id) {
        return new ResponseEntity<>(tweetService.getMessagesByTweetId(id), HttpStatus.OK);
    }

    @GetMapping("/tweets/{id}/markers")
    public ResponseEntity<List<MarkerResponseTo>> findAllMarkersByTweetId(@PathVariable Long id) {
        return new ResponseEntity<>(tweetService.getAllMarkersByTweetId(id), HttpStatus.OK);
    }
}
