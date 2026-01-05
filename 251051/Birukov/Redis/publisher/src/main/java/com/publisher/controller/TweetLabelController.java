package com.publisher.controller;

import com.publisher.dto.request.TweetLabelRequestTo;
import com.publisher.dto.response.TweetLabelResponseTo;
import com.publisher.service.TweetLabelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tweetlabels")
public class TweetLabelController {
	
	@Autowired
	private final TweetLabelService tweetLabelService;
	
	public TweetLabelController(TweetLabelService tweetLabelService) {
		this.tweetLabelService = tweetLabelService;
	}
	
	@PostMapping
	public ResponseEntity<TweetLabelResponseTo> create(@Valid @RequestBody TweetLabelRequestTo request) {
		TweetLabelResponseTo response = tweetLabelService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<TweetLabelResponseTo> findById(@PathVariable Long id) {
		TweetLabelResponseTo response = tweetLabelService.findById(id);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping
	public ResponseEntity<List<TweetLabelResponseTo>> findAll() {
		List<TweetLabelResponseTo> responses = tweetLabelService.findAll();
		return ResponseEntity.ok(responses);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<TweetLabelResponseTo> update(@PathVariable Long id, 
												 @Valid @RequestBody TweetLabelRequestTo request) {
		TweetLabelResponseTo response = tweetLabelService.update(id, request);
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		tweetLabelService.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/tweet/{tweetId}")
	public ResponseEntity<List<TweetLabelResponseTo>> findByTweetId(@PathVariable Long tweetId) {
		List<TweetLabelResponseTo> responses = tweetLabelService.findByTweetId(tweetId);
		return ResponseEntity.ok(responses);
	}
	
	@GetMapping("/label/{labelId}")
	public ResponseEntity<List<TweetLabelResponseTo>> findByLabelId(@PathVariable Long labelId) {
		List<TweetLabelResponseTo> responses = tweetLabelService.findByTweetId(labelId);
		return ResponseEntity.ok(responses);
	}
}