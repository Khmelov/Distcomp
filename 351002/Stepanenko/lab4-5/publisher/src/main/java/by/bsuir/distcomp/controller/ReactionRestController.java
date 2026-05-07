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

    @GetMapping
    public ResponseEntity<List<ReactionResponseTo>> getAll() {
        return ResponseEntity.ok(reactionService.getAll());
    }

    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<ReactionResponseTo>> getByTweetId(@PathVariable Long tweetId) {
        // Теперь этот метод существует в ReactionService
        return ResponseEntity.ok(reactionService.getByTweetId(tweetId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReactionResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reactionService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ReactionResponseTo> create(@Valid @RequestBody ReactionRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reactionService.create(request));
    }

    @PutMapping
    public ResponseEntity<ReactionResponseTo> update(@Valid @RequestBody ReactionRequestTo request) {
        return ResponseEntity.ok(reactionService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reactionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
