package com.example.task330.publisher.controller;

import com.example.task330.publisher.dto.ReactionRequestTo;
import com.example.task330.publisher.dto.ReactionResponseTo;
import com.example.task330.publisher.kafka.ReactionKafkaService;
import com.example.task330.publisher.service.RedisCacheService;
import com.example.task330.publisher.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// REST-контроллер передаёт запросы для работы с реакциями через Kafka.
@RestController
@RequestMapping("/api/v1.0/reactions")
public class ReactionController {

    private final ReactionKafkaService reactionKafkaService;
    private final TopicService topicService;
    private final RedisCacheService redisCacheService;

    // Spring передаёт необходимые зависимости через конструктор.
    public ReactionController(
            ReactionKafkaService reactionKafkaService,
            TopicService topicService,
            RedisCacheService redisCacheService) {
        this.reactionKafkaService = reactionKafkaService;
        this.topicService = topicService;
        this.redisCacheService = redisCacheService;
    }

    // Получение списка реакций с использованием Redis.
    @GetMapping
    public ResponseEntity<List<ReactionResponseTo>> findAll() {
        String cacheKey = "reactions:list";

        Optional<List<ReactionResponseTo>> cached =
                redisCacheService.get(
                        cacheKey,
                        new TypeReference<List<ReactionResponseTo>>() {}
                );

        if (cached.isPresent()) {
            return ResponseEntity.ok(cached.get());
        }

        List<ReactionResponseTo> reactions =
                reactionKafkaService.findAll();

        redisCacheService.save(cacheKey, reactions);

        return ResponseEntity.ok(reactions);
    }

    // Получение реакции по идентификатору с использованием Redis.
    @GetMapping("/{id}")
    public ResponseEntity<ReactionResponseTo> findById(
            @PathVariable Long id) {

        String cacheKey = "reaction:" + id;

        Optional<ReactionResponseTo> cached =
                redisCacheService.get(
                        cacheKey,
                        ReactionResponseTo.class
                );

        if (cached.isPresent()) {
            return ResponseEntity.ok(cached.get());
        }

        Optional<ReactionResponseTo> reaction =
                reactionKafkaService.findById(id);

        reaction.ifPresent(value ->
                redisCacheService.save(cacheKey, value)
        );

        return reaction
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Создание реакции с проверкой существования темы.
    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody ReactionRequestTo request) {

        if (topicService.findById(request.getTopicId()).isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Topic not found"));
        }

        // Новая реакция отправляется в Kafka со статусом PENDING.
        ReactionResponseTo createdReaction =
                reactionKafkaService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdReaction);
    }

    // Обновление реакции с проверкой существования темы.
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @Valid @RequestBody ReactionRequestTo request) {

        if (topicService.findById(request.getTopicId()).isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Topic not found"));
        }

        Optional<ReactionResponseTo> updatedReaction =
                reactionKafkaService.update(id, request);

        return updatedReaction
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Удаление реакции по идентификатору через Kafka.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        boolean deleted = reactionKafkaService.deleteById(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}
