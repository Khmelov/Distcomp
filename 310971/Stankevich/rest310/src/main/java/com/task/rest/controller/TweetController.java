package com.task.rest.controller;

import com.task.rest.dto.TweetRequestTo;
import com.task.rest.dto.TweetResponseTo;
import com.task.rest.service.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tweets")
@RequiredArgsConstructor
public class TweetController {

    private final TweetService service;

    @PostMapping
    public ResponseEntity<TweetResponseTo> create(@Valid @RequestBody TweetRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TweetResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<TweetResponseTo>> getAll(
            @RequestParam(required = false) List<String> markNames,
            @RequestParam(required = false) List<Long> markIds,
            @RequestParam(required = false) String authorLogin,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content) {

        if (markNames != null || markIds != null || authorLogin != null ||
                title != null || content != null) {
            return ResponseEntity.ok(service.search(markNames, markIds, authorLogin, title, content));
        }

        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping
    public ResponseEntity<TweetResponseTo> update(@Valid @RequestBody TweetRequestTo request) {
        return ResponseEntity.ok(service.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{tweetId}/marks/{markId}")
    public ResponseEntity<Void> addMark(@PathVariable Long tweetId, @PathVariable Long markId) {
        service.addMarkToTweet(tweetId, markId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{tweetId}/marks/{markId}")
    public ResponseEntity<Void> removeMark(@PathVariable Long tweetId, @PathVariable Long markId) {
        service.removeMarkFromTweet(tweetId, markId);
        return ResponseEntity.noContent().build();
    }
}