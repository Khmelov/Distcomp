package org.ex.distributed_computing.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ex.distributed_computing.dto.request.TweetRequestDTO;
import org.ex.distributed_computing.dto.response.TweetResponseDTO;
import org.ex.distributed_computing.service.TweetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tweets")
@RequiredArgsConstructor
public class TweetController {

  private final TweetService tweetService;

  @GetMapping
  public List<TweetResponseDTO> getAllTweet() {
    return tweetService.getAllTweets();
  }

  @GetMapping("/{id}")
  public ResponseEntity<TweetResponseDTO> getTweetById(@PathVariable Long id) {
    return ResponseEntity.ok(tweetService.getTweetById(id));
  }

  @PostMapping
  public ResponseEntity<TweetResponseDTO> createTweet(@Valid @RequestBody TweetRequestDTO requestDTO) {
    return ResponseEntity.status(HttpStatus.CREATED).body(tweetService.createTweet(requestDTO));
  }

  @PutMapping
  public ResponseEntity<TweetResponseDTO> updateTweet(@Valid @RequestBody TweetRequestDTO requestDTO) {
    return ResponseEntity.ok(tweetService.updateTweet(requestDTO));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTweet(@PathVariable Long id) {
    tweetService.deleteTweet(id);
    return ResponseEntity.noContent().build();
  }
}

