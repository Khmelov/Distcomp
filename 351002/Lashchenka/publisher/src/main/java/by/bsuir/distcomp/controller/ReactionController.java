package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.ReactionRequestTo;
import by.bsuir.distcomp.dto.response.ReactionResponseTo;
import by.bsuir.distcomp.reaction.ReactionKafkaGateway;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/reactions")
public class ReactionController {

    private final ReactionKafkaGateway reactionKafkaGateway;

    public ReactionController(ReactionKafkaGateway reactionKafkaGateway) {
        this.reactionKafkaGateway = reactionKafkaGateway;
    }

    @PostMapping
    public ResponseEntity<ReactionResponseTo> create(@Valid @RequestBody ReactionRequestTo dto) {
        return reactionKafkaGateway.create(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReactionResponseTo> getById(@PathVariable Long id) {
        return reactionKafkaGateway.getById(id);
    }

    @GetMapping
    public ResponseEntity<List<ReactionResponseTo>> getAll() {
        return reactionKafkaGateway.getAll();
    }

    @PutMapping
    public ResponseEntity<ReactionResponseTo> update(@Valid @RequestBody ReactionRequestTo dto) {
        return reactionKafkaGateway.update(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        return reactionKafkaGateway.deleteById(id);
    }
}
