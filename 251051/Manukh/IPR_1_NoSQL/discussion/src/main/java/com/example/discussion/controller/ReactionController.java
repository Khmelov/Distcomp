package com.example.discussion.controller;

import com.example.discussion.entity.Reaction;
import com.example.discussion.service.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0")
public class ReactionController {

    @Autowired
    private ReactionService reactionService;

    @GetMapping("/reactions")
    public List<Reaction> getAllReactions() {
        return reactionService.getAllReactions();
    }

    @GetMapping("/reactions/{id}")
    public ResponseEntity<Reaction> getReactionById(@PathVariable String id) {
        Reaction reaction = reactionService.getReactionById(id);
        if (reaction != null) {
            return ResponseEntity.ok(reaction);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/reactions")
    public ResponseEntity<Reaction> createReaction(@RequestBody Map<String, Object> request) {
        Long storyId = Long.valueOf(request.get("storyId").toString());
        String content = request.get("content").toString();

        Reaction reaction = reactionService.createReaction(storyId, content);
        return ResponseEntity.status(HttpStatus.CREATED).body(reaction);
    }

    @DeleteMapping("/reactions/{id}")
    public ResponseEntity<Void> deleteReaction(@PathVariable String id) {
        reactionService.deleteReaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reactions/story/{storyId}")
    public List<Reaction> getReactionsByStoryId(@PathVariable Long storyId) {
        return reactionService.getReactionsByStoryId(storyId);
    }
}