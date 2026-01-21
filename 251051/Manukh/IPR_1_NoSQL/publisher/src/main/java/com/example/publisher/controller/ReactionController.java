// ReactionController.java
package com.example.publisher.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.publisher.dto.request.ReactionRequestTo;
import com.example.publisher.dto.response.ReactionResponseTo;
import com.example.publisher.service.ReactionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
public class ReactionController {

    @Autowired
    private ReactionService reactionService;

    @GetMapping("/reactions")
    public ResponseEntity<List<ReactionResponseTo>> getAllReactions() {
        return ResponseEntity.ok(reactionService.getAllReactions());
    }

    @GetMapping("/reactions/{id}")
    public ResponseEntity<ReactionResponseTo> getReactionById(@PathVariable Long id) {
        return ResponseEntity.ok(reactionService.getReactionById(id));
    }

    @PostMapping("/reactions")
    public ResponseEntity<ReactionResponseTo> createReaction(@Valid @RequestBody ReactionRequestTo request) {
        ReactionResponseTo response = reactionService.createReaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/reactions/{id}")
    public ResponseEntity<ReactionResponseTo> updateReaction(
            @PathVariable Long id,
            @Valid @RequestBody ReactionRequestTo request) {
        ReactionResponseTo response = reactionService.updateReaction(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/reactions/{id}")
    public ResponseEntity<Void> deleteReaction(@PathVariable Long id) {
        reactionService.deleteReaction(id);
        return ResponseEntity.noContent().build();
    }

    // Additional endpoints for relationships
    @GetMapping("/stories/{storyId}/reactions")
    public ResponseEntity<List<ReactionResponseTo>> getReactionsByStoryId(@PathVariable Long storyId) {
        return ResponseEntity.ok(reactionService.getReactionsByStoryId(storyId));
    }
}