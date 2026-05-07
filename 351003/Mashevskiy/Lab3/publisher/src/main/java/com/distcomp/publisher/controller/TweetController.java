package com.distcomp.publisher.controller;

import com.distcomp.publisher.dto.TweetRequestDTO;
import com.distcomp.publisher.dto.TweetResponseDTO;
import com.distcomp.publisher.service.TweetService;
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
    private TweetService tweetService;

    @GetMapping
    public ResponseEntity<List<TweetResponseDTO>> getAll() {
        return ResponseEntity.ok(tweetService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TweetResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tweetService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TweetResponseDTO> create(@Valid @RequestBody TweetRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tweetService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TweetResponseDTO> update(@PathVariable Long id, @Valid @RequestBody TweetRequestDTO dto) {
        return ResponseEntity.ok(tweetService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tweetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}