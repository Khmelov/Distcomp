package by.bsuir.task361.publisher.controller;

import by.bsuir.task361.publisher.dto.request.ReactionRequestTo;
import by.bsuir.task361.publisher.dto.response.ReactionResponseTo;
import by.bsuir.task361.publisher.service.SecuredReactionService;
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
@RequestMapping("/api/v2.0/reactions")
public class ReactionV2Controller {
    private final SecuredReactionService securedReactionService;

    public ReactionV2Controller(SecuredReactionService securedReactionService) {
        this.securedReactionService = securedReactionService;
    }

    @PostMapping
    public ResponseEntity<ReactionResponseTo> create(@Valid @RequestBody ReactionRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(securedReactionService.create(request));
    }

    @GetMapping
    public List<ReactionResponseTo> findAll() {
        return securedReactionService.findAll();
    }

    @GetMapping("/{id}")
    public ReactionResponseTo findById(@PathVariable Long id) {
        return securedReactionService.findById(id);
    }

    @PutMapping
    public ReactionResponseTo update(@Valid @RequestBody ReactionRequestTo request) {
        return securedReactionService.update(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        securedReactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
