package com.blog.controller;

import com.blog.dto.ReactionRequestTo;
import com.blog.dto.ReactionResponseTo;
import com.blog.service.ReactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/reactions")
public class ReactionController {

    private final ReactionService reactionService;

    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @GetMapping
    public ResponseEntity<List<ReactionResponseTo>> getAllReactions() {
        List<ReactionResponseTo> reactions = reactionService.findAll();
        return ResponseEntity.ok(reactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReactionResponseTo> getReactionById(@PathVariable Long id) {
        ReactionResponseTo reaction = reactionService.findById(id);
        return ResponseEntity.ok(reaction);
    }

    @PostMapping
    public ResponseEntity<ReactionResponseTo> createReaction(@Valid @RequestBody ReactionRequestTo request) {
        ReactionResponseTo createdReaction = reactionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReactionResponseTo> updateReaction(@PathVariable Long id, @Valid @RequestBody ReactionRequestTo request) {
        ReactionResponseTo updatedReaction = reactionService.update(id, request);
        return ResponseEntity.ok(updatedReaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReaction(@PathVariable Long id) {
        reactionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}