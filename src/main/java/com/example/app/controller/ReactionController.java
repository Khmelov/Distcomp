package com.example.app.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.app.dto.ReactionRequestDTO;
import com.example.app.dto.ReactionResponseDTO;
import com.example.app.service.ReactionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/reactions")
public class ReactionController {
    private final ReactionService service;

    public ReactionController(ReactionService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ReactionResponseDTO>> getReactions() {
        return ResponseEntity.ok(service.getAllReactions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReactionResponseDTO> getReaction(@PathVariable Long id) {
        return ResponseEntity.ok(service.getReactionById(id));
    }

    @PostMapping
    public ResponseEntity<ReactionResponseDTO> createReaction(@Valid @RequestBody ReactionRequestDTO request) {
        ReactionResponseDTO created = service.createReaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping
    public ResponseEntity<ReactionResponseDTO> updateReaction(@Valid @RequestBody ReactionRequestDTO request) {
        return ResponseEntity.ok(service.updateReaction(request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReaction(@PathVariable Long id) {
        service.deleteReaction(id);
    }
}