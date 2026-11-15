
package com.example.task320.controller;

import com.example.task320.dto.ReactionRequestTo;
import com.example.task320.dto.ReactionResponseTo;
import com.example.task320.entity.Reaction;
import com.example.task320.exception.ResourceNotFoundException;
import com.example.task320.service.ReactionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Контроллер обрабатывает REST-запросы для работы с реакциями.
@RestController
@RequestMapping("/api/v1.0/reactions")
public class ReactionController {

    private final ReactionService reactionService;

    // Spring передаёт сервис через конструктор.
    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    // Получение списка реакций с пагинацией, сортировкой и фильтрацией.
    @GetMapping
    public ResponseEntity<List<ReactionResponseTo>> findAll(
            Pageable pageable,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String content) {

        // Создаётся базовое условие, которому соответствуют все записи.
        Specification<Reaction> specification =
                (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        // Фильтрация по идентификатору темы.
        if (topicId != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(
                            root.get("topic").get("id"),
                            topicId
                    ));
        }

        // Фильтрация по содержимому реакции.
        if (content != null && !content.isBlank()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("content")),
                            "%" + content.trim().toLowerCase() + "%"
                    ));
        }

        // Из страницы извлекается список реакций для JSON-массива.
        Page<ReactionResponseTo> page =
                reactionService.findAll(specification, pageable);

        return ResponseEntity.ok(page.getContent());
    }

    // Получение реакции по идентификатору.
    @GetMapping("/{id}")
    public ResponseEntity<ReactionResponseTo> findById(@PathVariable Long id) {
        ReactionResponseTo reaction = reactionService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reaction with id " + id + " not found"
                ));

        return ResponseEntity.ok(reaction);
    }

    // Получение всех реакций определённой темы.
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<ReactionResponseTo>> findByTopicId(
            @PathVariable Long topicId) {

        return ResponseEntity.ok(reactionService.findByTopicId(topicId));
    }

    // Создание новой реакции.
    @PostMapping
    public ResponseEntity<ReactionResponseTo> create(
            @Valid @RequestBody ReactionRequestTo request) {

        ReactionResponseTo createdReaction = reactionService.save(request)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Topic with id " + request.getTopicId() + " not found"
                ));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdReaction);
    }

    // Обновление существующей реакции.
    @PutMapping("/{id}")
    public ResponseEntity<ReactionResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody ReactionRequestTo request) {

        ReactionResponseTo updatedReaction = reactionService.update(id, request)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reaction or topic not found"
                ));

        return ResponseEntity.ok(updatedReaction);
    }

    // Удаление реакции по идентификатору.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        if (!reactionService.deleteById(id)) {
            throw new ResourceNotFoundException(
                    "Reaction with id " + id + " not found"
            );
        }

        return ResponseEntity.noContent().build();
    }
}
