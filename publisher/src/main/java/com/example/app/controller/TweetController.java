package com.example.app.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.app.dto.TweetRequestDTO;
import com.example.app.dto.TweetResponseDTO;
import com.example.app.service.TweetService;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetController {
    private final TweetService service;

    public TweetController(TweetService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TweetResponseDTO>> getTweets() {
        return ResponseEntity.ok(service.getAllTweets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TweetResponseDTO> getTweet(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTweetById(id));
    }

    // НОВЫЙ ЭНДПОИНТ: получить твит с реакциями
    @GetMapping("/{id}/with-reactions")
    public ResponseEntity<TweetResponseDTO> getTweetWithReactions(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTweetWithReactions(id));
    }

    @PostMapping
    public ResponseEntity<TweetResponseDTO> createTweet(@Valid @RequestBody TweetRequestDTO request) {
        TweetResponseDTO created = service.createTweet(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping
    public ResponseEntity<TweetResponseDTO> updateTweet(@Valid @RequestBody TweetRequestDTO request) {
        return ResponseEntity.ok(service.updateTweet(request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTweet(@PathVariable Long id) {
        service.deleteTweet(id);
    }
}