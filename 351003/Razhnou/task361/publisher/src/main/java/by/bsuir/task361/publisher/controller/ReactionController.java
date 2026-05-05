package by.bsuir.task361.publisher.controller;

import by.bsuir.task361.publisher.dto.request.ReactionRequestTo;
import by.bsuir.task361.publisher.dto.response.ReactionResponseTo;
import by.bsuir.task361.publisher.service.ReactionKafkaGateway;
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
    private final ReactionKafkaGateway reactionKafkaGateway;

    public ReactionController(ReactionKafkaGateway reactionKafkaGateway) {
        this.reactionKafkaGateway = reactionKafkaGateway;
    }

    @PostMapping
    public ResponseEntity<ReactionResponseTo> create(@Valid @RequestBody ReactionRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reactionKafkaGateway.create(request));
    }

    @GetMapping
    public List<ReactionResponseTo> findAll() {
        return reactionKafkaGateway.findAll();
    }

    @GetMapping("/{id}")
    public ReactionResponseTo findById(@PathVariable Long id) {
        return reactionKafkaGateway.findById(id);
    }

    @PutMapping
    public ReactionResponseTo update(@Valid @RequestBody ReactionRequestTo request) {
        return reactionKafkaGateway.update(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reactionKafkaGateway.delete(id);
        return ResponseEntity.noContent().build();
    }
}
