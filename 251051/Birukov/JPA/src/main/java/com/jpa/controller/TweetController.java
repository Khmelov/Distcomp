package com.jpa.controller;

import com.jpa.dto.request.TweetRequestTo;
import com.jpa.dto.response.TweetResponseTo;
import com.jpa.service.TweetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetController {
	
	@Autowired
	private final TweetService tweetService;
	
	public TweetController(TweetService tweetService) {
		this.tweetService = tweetService;
	}
	
	@PostMapping
	public ResponseEntity<TweetResponseTo> create(@Valid @RequestBody TweetRequestTo request) {
		TweetResponseTo response = tweetService.create(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<TweetResponseTo> findById(@PathVariable Long id) {
		TweetResponseTo response = tweetService.findById(id);
		return ResponseEntity.ok(response);
	}
	
	@GetMapping
	public ResponseEntity<List<TweetResponseTo>> findAll() {
		List<TweetResponseTo> responses = tweetService.findAll();
		return ResponseEntity.ok(responses);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<TweetResponseTo> update(@PathVariable Long id, 
												  @Valid @RequestBody TweetRequestTo request) {
		TweetResponseTo response = tweetService.update(id, request);
		return ResponseEntity.ok(response);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		tweetService.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping("/writer/{writerId}")
	public ResponseEntity<List<TweetResponseTo>> findByWriterId(@PathVariable Long writerId) {
		List<TweetResponseTo> responses = tweetService.findByWriterId(writerId);
		return ResponseEntity.ok(responses);
	}
}