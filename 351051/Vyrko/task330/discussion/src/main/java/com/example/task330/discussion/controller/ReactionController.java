package com.example.task330.discussion.controller;

import com.example.task330.discussion.dto.ReactionRequestTo;
import com.example.task330.discussion.dto.ReactionResponseTo;
import com.example.task330.discussion.service.ReactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// REST-контроллер обрабатывает запросы для работы с реакциями.
@RestController
@RequestMapping("/api/v1.0/reactions")
public class ReactionController {

    private final ReactionService reactionService;

    // Spring передаёт сервис через конструктор.
    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    // Получение всех реакций.
    @GetMapping
    public ResponseEntity<List<ReactionResponseTo>> findAll() {
        return ResponseEntity.ok(reactionService.findAll());
    }

    // Получение всех реакций определённой темы.
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<ReactionResponseTo>> findByTopicId(
            @PathVariable Long topicId) {
        return ResponseEntity.ok(reactionService.findByTopicId(topicId));
    }

    // Получение реакции по идентификатору.
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        return reactionService.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Reaction not found")));
    }

    // Создание новой реакции.
    @PostMapping
    public ResponseEntity<ReactionResponseTo> create(
            @Valid @RequestBody ReactionRequestTo request) {
        ReactionResponseTo createdReaction = reactionService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdReaction);
    }

    // Обновление существующей реакции.
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody ReactionRequestTo request) {
        return reactionService.update(id, request)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Reaction not found")));
    }

    // Удаление реакции по идентификатору.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!reactionService.deleteById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Reaction not found"));
        }

        return ResponseEntity.noContent().build();
    }

    // Удаление всех реакций определённой темы.
    @DeleteMapping("/topic/{topicId}")
    public ResponseEntity<Void> deleteByTopicId(@PathVariable Long topicId) {
        reactionService.deleteByTopicId(topicId);
        return ResponseEntity.noContent().build();
    }
}
