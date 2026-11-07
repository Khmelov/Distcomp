
package com.example.task320.controller;

import com.example.task320.dto.MarkerResponseTo;
import com.example.task320.dto.TopicRequestTo;
import com.example.task320.dto.TopicResponseTo;
import com.example.task320.entity.Topic;
import com.example.task320.exception.ResourceNotFoundException;
import com.example.task320.service.EditorService;
import com.example.task320.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Контроллер обрабатывает REST-запросы для работы с темами.
@RestController
@RequestMapping("/api/v1.0/topics")
public class TopicController {

    // Сервис выполняет бизнес-логику работы с темами.
    private final TopicService topicService;

    // Сервис редакторов используется для проверки существования автора.
    private final EditorService editorService;

    // Spring передаёт необходимые сервисы через конструктор.
    public TopicController(
            TopicService topicService,
            EditorService editorService) {
        this.topicService = topicService;
        this.editorService = editorService;
    }

    // Получение списка тем с пагинацией, сортировкой и фильтрацией.
    @GetMapping
    public ResponseEntity<List<TopicResponseTo>> findAll(
            Pageable pageable,
            @RequestParam(required = false) Long editorId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content) {

        // Создаётся базовое условие, которому соответствуют все записи.
        Specification<Topic> specification =
                (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        // Фильтрация по идентификатору редактора.
        if (editorId != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(
                            root.get("editor").get("id"),
                            editorId
                    ));
        }

        // Фильтрация по заголовку темы.
        if (title != null && !title.isBlank()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("title")),
                            "%" + title.trim().toLowerCase() + "%"
                    ));
        }

        // Фильтрация по содержимому темы.
        if (content != null && !content.isBlank()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("content")),
                            "%" + content.trim().toLowerCase() + "%"
                    ));
        }

        // Из страницы извлекается список тем для JSON-массива.
        Page<TopicResponseTo> page =
                topicService.findAll(specification, pageable);

        return ResponseEntity.ok(page.getContent());
    }

    // Получение темы по идентификатору.
    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseTo> findById(@PathVariable Long id) {
        TopicResponseTo topic = topicService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Topic with id " + id + " not found"
                ));

        return ResponseEntity.ok(topic);
    }

    // Получение всех тем определённого редактора.
    @GetMapping("/editor/{editorId}")
    public ResponseEntity<List<TopicResponseTo>> findByEditorId(
            @PathVariable Long editorId) {

        if (editorService.findById(editorId).isEmpty()) {
            throw new ResourceNotFoundException(
                    "Editor with id " + editorId + " not found"
            );
        }

        return ResponseEntity.ok(topicService.findByEditorId(editorId));
    }

    // Создание новой темы.
    @PostMapping
    public ResponseEntity<TopicResponseTo> create(
            @Valid @RequestBody TopicRequestTo request) {

        TopicResponseTo createdTopic = topicService.save(request)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Editor with id " + request.getEditorId() + " not found"
                ));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdTopic);
    }

    // Обновление существующей темы.
    @PutMapping("/{id}")
    public ResponseEntity<TopicResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody TopicRequestTo request) {

        TopicResponseTo updatedTopic = topicService.update(id, request)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Topic or editor not found"
                ));

        return ResponseEntity.ok(updatedTopic);
    }

    // Получение списка маркеров определённой темы.
    @GetMapping("/{topicId}/markers")
    public ResponseEntity<List<MarkerResponseTo>> findMarkers(
            @PathVariable Long topicId) {

        List<MarkerResponseTo> markers = topicService.findMarkers(topicId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Topic with id " + topicId + " not found"
                ));

        return ResponseEntity.ok(markers);
    }

    // Добавление существующего маркера к теме.
    @PostMapping("/{topicId}/markers/{markerId}")
    public ResponseEntity<Void> addMarker(
            @PathVariable Long topicId,
            @PathVariable Long markerId) {

        if (!topicService.addMarker(topicId, markerId)) {
            throw new ResourceNotFoundException(
                    "Topic or marker not found"
            );
        }

        return ResponseEntity.noContent().build();
    }

    // Удаление маркера из темы.
    @DeleteMapping("/{topicId}/markers/{markerId}")
    public ResponseEntity<Void> removeMarker(
            @PathVariable Long topicId,
            @PathVariable Long markerId) {

        if (!topicService.removeMarker(topicId, markerId)) {
            throw new ResourceNotFoundException(
                    "Topic or marker not found in topic"
            );
        }

        return ResponseEntity.noContent().build();
    }

    // Удаление темы по идентификатору.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        if (!topicService.deleteById(id)) {
            throw new ResourceNotFoundException(
                    "Topic with id " + id + " not found"
            );
        }

        return ResponseEntity.noContent().build();
    }
}
