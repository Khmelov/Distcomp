package com.task.rest.controller;

import com.task.rest.dto.TweetRequestTo;
import com.task.rest.dto.TweetResponseTo;
import com.task.rest.service.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tweets")
@RequiredArgsConstructor
public class TweetController {

    private final TweetService tweetService;

    @GetMapping("/{id}")
    public ResponseEntity<TweetResponseTo> getById(@PathVariable Long id) {
        log.info("GET /api/v1.0/tweets/{}", id);
        return ResponseEntity.ok(tweetService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<TweetResponseTo>> getAll() {
        log.info("GET /api/v1.0/tweets");
        return ResponseEntity.ok(tweetService.getAllList());
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<TweetResponseTo>> getByAuthorId(@PathVariable Long authorId) {
        log.info("GET /api/v1.0/tweets/author/{}", authorId);
        return ResponseEntity.ok(tweetService.getByAuthorId(authorId));
    }

    @PostMapping
    public ResponseEntity<TweetResponseTo> create(@Valid @RequestBody TweetRequestTo request) {
        log.info("POST /api/v1.0/tweets");
        return ResponseEntity.status(HttpStatus.CREATED).body(tweetService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TweetResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody TweetRequestTo request) {
        log.info("PUT /api/v1.0/tweets/{}", id);
        return ResponseEntity.ok(tweetService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1.0/tweets/{}", id);
        tweetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
