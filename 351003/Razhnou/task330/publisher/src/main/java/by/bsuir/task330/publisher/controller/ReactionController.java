package by.bsuir.task330.publisher.controller;

import by.bsuir.task330.publisher.dto.request.ReactionRequestTo;
import by.bsuir.task330.publisher.dto.response.ReactionResponseTo;
import by.bsuir.task330.publisher.service.DiscussionReactionClient;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/reactions")
public class ReactionController {
    private final DiscussionReactionClient discussionReactionClient;

    public ReactionController(DiscussionReactionClient discussionReactionClient) {
        this.discussionReactionClient = discussionReactionClient;
    }

    @PostMapping
    public ResponseEntity<ReactionResponseTo> create(@Valid @RequestBody ReactionRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(discussionReactionClient.create(request));
    }

    @GetMapping
    public List<ReactionResponseTo> findAll() {
        return discussionReactionClient.findAll();
    }

    @GetMapping("/{id}")
    public ReactionResponseTo findById(@PathVariable Long id) {
        return discussionReactionClient.findById(id);
    }

    @PutMapping
    public ReactionResponseTo update(@Valid @RequestBody ReactionRequestTo request) {
        return discussionReactionClient.update(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        discussionReactionClient.delete(id);
        return ResponseEntity.noContent().build();
    }
}
