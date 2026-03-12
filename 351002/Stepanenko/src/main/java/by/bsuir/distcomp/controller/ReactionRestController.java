package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.core.service.ReactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/reactions")
public class ReactionRestController {
    private final ReactionService reactionService;

    public ReactionRestController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    @PostMapping
    public ResponseEntity<ReactionResponseTo> create(@Valid @RequestBody ReactionRequestTo createRequest) {
        ReactionResponseTo createdReaction = reactionService.create(createRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdReaction);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReactionResponseTo> getById(@PathVariable("id") Long reactionId) {
        ReactionResponseTo reaction = reactionService.getById(reactionId);
        return ResponseEntity
                .ok(reaction);
    }

    @GetMapping
    public ResponseEntity<List<ReactionResponseTo>> getAll() {
        List<ReactionResponseTo> reactions = reactionService.getAll();
        return ResponseEntity
                .ok(reactions);
    }

    @PutMapping
    public ResponseEntity<ReactionResponseTo> update(@Valid @RequestBody ReactionRequestTo updateRequest) {
        ReactionResponseTo updatedReaction = reactionService.update(updateRequest);
        return ResponseEntity
                .ok(updatedReaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long reactionId) {
        reactionService.deleteById(reactionId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<ReactionResponseTo>> getReactionsByTweetId(@PathVariable("tweetId") Long tweetId) {
        List<ReactionResponseTo> reactions = reactionService.getReactionsByTweetId(tweetId);
        return ResponseEntity
                .ok(reactions);
    }
}
