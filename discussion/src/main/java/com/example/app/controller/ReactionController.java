package com.example.app.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.app.dto.ReactionRequestDTO;
import com.example.app.dto.ReactionResponseDTO;
import com.example.app.service.ReactionService;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/reactions")
public class ReactionController {
    private final ReactionService service;

    public ReactionController(ReactionService service) {
        this.service = service;
    }

    // 1. Получить все реакции (возможно, с пагинацией)
    @GetMapping
    public ResponseEntity<List<ReactionResponseDTO>> getAllReactions() {
        // Внимание: в Cassandra получение всех записей - дорогая операция
        // Лучше добавить пагинацию или ограничение
        return ResponseEntity.ok(service.getAllReactions());
    }

    // 2. Получить реакцию по ID (требует tweetId)
    @GetMapping("/{id}")
    public ResponseEntity<ReactionResponseDTO> getReaction(
            @PathVariable Long id,
            @RequestParam Long tweetId,
            @RequestParam(required = false, defaultValue = "global") String country) {
        return ResponseEntity.ok(service.getReactionById(country, tweetId, id));
    }

    // 3. Получить все реакции для твита
    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<ReactionResponseDTO>> getReactionsByTweetId(
            @PathVariable Long tweetId,
            @RequestParam(required = false, defaultValue = "global") String country) {
        return ResponseEntity.ok(service.getReactionsByTweetId(country, tweetId));
    }

    // 4. Создать реакцию
    @PostMapping
    public ResponseEntity<ReactionResponseDTO> createReaction(
            @Valid @RequestBody ReactionRequestDTO request) {
        ReactionResponseDTO created = service.createReaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // 5. Обновить реакцию
    @PutMapping("/{id}")
    public ResponseEntity<ReactionResponseDTO> updateReaction(
            @PathVariable Long id,
            @RequestParam Long tweetId,
            @RequestParam(required = false, defaultValue = "global") String country,
            @Valid @RequestBody ReactionRequestDTO request) {
        return ResponseEntity.ok(service.updateReaction(country, tweetId, id, request));
    }

    // 6. Удалить реакцию
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReaction(
            @PathVariable Long id,
            @RequestParam Long tweetId,
            @RequestParam(required = false, defaultValue = "global") String country) {
        service.deleteReaction(country, tweetId, id);
    }

    // 7. Удалить все реакции твита
    @DeleteMapping("/tweet/{tweetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReactionsByTweetId(
            @PathVariable Long tweetId,
            @RequestParam(required = false, defaultValue = "global") String country) {
        service.deleteReactionsByTweetId(country, tweetId);
    }
}