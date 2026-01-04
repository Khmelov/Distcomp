package com.task.rest.controller;

import com.task.rest.dto.TweetRequestTo;
import com.task.rest.dto.TweetResponseTo;
import com.task.rest.service.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1.0/tweets")
@RequiredArgsConstructor
public class TweetController {

    private final TweetService tweetService;

    @GetMapping("/{id}")
    public ResponseEntity<TweetResponseTo> getById(@PathVariable Long id) {
        log.info("GET request for tweet with id: {}", id);
        return ResponseEntity.ok(tweetService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<TweetResponseTo>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET request for all tweets");
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(tweetService.getAll(pageable).getContent());
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<TweetResponseTo>> getByAuthorId(@PathVariable Long authorId) {
        log.info("GET request for tweets by author id: {}", authorId);
        return ResponseEntity.ok(tweetService.getByAuthorId(authorId));
    }

    @PostMapping
    public ResponseEntity<TweetResponseTo> create(@Valid @RequestBody TweetRequestTo requestTo) {
        log.info("POST request to create tweet");
        return ResponseEntity.status(HttpStatus.CREATED).body(tweetService.create(requestTo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TweetResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody TweetRequestTo requestTo) {
        log.info("PUT request to update tweet with id: {}", id);
        return ResponseEntity.ok(tweetService.update(id, requestTo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE request for tweet with id: {}", id);
        tweetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
