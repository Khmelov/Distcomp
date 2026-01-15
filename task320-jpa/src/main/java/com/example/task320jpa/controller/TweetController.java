package com.example.task320jpa.controller;

import com.example.task320jpa.dto.request.TweetRequestTo;
import com.example.task320jpa.dto.response.TweetResponseTo;
import com.example.task320jpa.service.TweetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0/tweets")
@RequiredArgsConstructor
public class TweetController {
    private final TweetService tweetService;
    
    @PostMapping
    public ResponseEntity<TweetResponseTo> create(@Valid @RequestBody TweetRequestTo requestTo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tweetService.create(requestTo));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TweetResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tweetService.getById(id));
    }
    
    @GetMapping
    public ResponseEntity<Page<TweetResponseTo>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        Pageable pageable = createPageable(page, size, sort);
        return ResponseEntity.ok(tweetService.getAll(pageable));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TweetResponseTo> update(@PathVariable Long id, @Valid @RequestBody TweetRequestTo requestTo) {
        return ResponseEntity.ok(tweetService.update(id, requestTo));
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<TweetResponseTo> partialUpdate(@PathVariable Long id, @RequestBody TweetRequestTo requestTo) {
        return ResponseEntity.ok(tweetService.partialUpdate(id, requestTo));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tweetService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    private Pageable createPageable(int page, int size, String[] sort) {
        if (sort.length == 2) {
            String field = sort[0];
            Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            return PageRequest.of(page, size, Sort.by(direction, field));
        }
        return PageRequest.of(page, size);
    }
}
