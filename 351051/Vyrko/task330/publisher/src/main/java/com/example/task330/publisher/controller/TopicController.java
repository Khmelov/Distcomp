package com.example.task330.publisher.controller;

import com.example.task330.publisher.dto.MarkerResponseTo;
import com.example.task330.publisher.dto.TopicRequestTo;
import com.example.task330.publisher.dto.TopicResponseTo;
import com.example.task330.publisher.entity.Topic;
import com.example.task330.publisher.exception.ResourceNotFoundException;
import com.example.task330.publisher.service.EditorService;
import com.example.task330.publisher.service.RedisCacheService;
import com.example.task330.publisher.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tools.jackson.core.type.TypeReference;

import java.util.List;

// Контроллер обрабатывает REST-запросы для работы с темами.
@RestController
@RequestMapping("/api/v1.0/topics")
public class TopicController {

    // Сервис выполняет бизнес-логику работы с темами.
    private final TopicService topicService;

    // Сервис редакторов используется для проверки существования автора.
    private final EditorService editorService;

    // Сервис обеспечивает кеширование данных в Redis.
    private final RedisCacheService redisCacheService;

    // Spring передаёт необходимые сервисы через конструктор.
    public TopicController(
            TopicService topicService,
            EditorService editorService,
            RedisCacheService redisCacheService) {
        this.topicService = topicService;
        this.editorService = editorService;
        this.redisCacheService = redisCacheService;
    }

    // Получение списка тем с пагинацией, сортировкой и фильтрацией.
    @GetMapping
    public ResponseEntity<List<TopicResponseTo>> findAll(
            Pageable pageable,
            @RequestParam(required = false) Long editorId,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content) {

        // Формируется ключ с учётом параметров запроса.
        String cacheKey = "topics:list:"
                + "page=" + pageable.getPageNumber()
                + ":size=" + pageable.getPageSize()
                + ":sort=" + pageable.getSort()
                + ":editorId=" + (editorId == null ? "" : editorId)
                + ":title=" + normalize(title)
                + ":content=" + normalize(content);

        // Проверяется наличие списка тем в Redis.
        var cachedTopics = redisCacheService.get(
                cacheKey,
                new TypeReference<List<TopicResponseTo>>() {}
        );

        if (cachedTopics.isPresent()) {
            return ResponseEntity.ok(cachedTopics.get());
        }

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

        List<TopicResponseTo> topics = page.getContent();

        // Полученный список сохраняется в Redis.
        redisCacheService.save(cacheKey, topics);

        return ResponseEntity.ok(topics);
    }

    // Получение темы по идентификатору.
    // Сначала выполняется поиск в Redis, затем в базе данных.
    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseTo> findById(@PathVariable Long id) {

        String cacheKey = "topic:" + id;

        // Проверяется наличие темы в Redis.
        var cachedTopic = redisCacheService.get(
                cacheKey,
                TopicResponseTo.class
        );

        if (cachedTopic.isPresent()) {
            return ResponseEntity.ok(cachedTopic.get());
        }

        // Если данных в кеше нет, выполняется запрос к базе данных.
        TopicResponseTo topic = topicService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Topic with id " + id + " not found"
                ));

        // Полученная тема сохраняется в Redis.
        redisCacheService.save(cacheKey, topic);

        return ResponseEntity.ok(topic);
    }

    // Получение всех тем определённого редактора.
    @GetMapping("/editor/{editorId}")
    public ResponseEntity<List<TopicResponseTo>> findByEditorId(
            @PathVariable Long editorId) {

        // Проверяется существование редактора.
        if (editorService.findById(editorId).isEmpty()) {
            throw new ResourceNotFoundException(
                    "Editor with id " + editorId + " not found"
            );
        }

        String cacheKey = "topics:editor:" + editorId;

        // Проверяется наличие списка тем редактора в Redis.
        var cachedTopics = redisCacheService.get(
                cacheKey,
                new TypeReference<List<TopicResponseTo>>() {}
        );

        if (cachedTopics.isPresent()) {
            return ResponseEntity.ok(cachedTopics.get());
        }

        // Если данных в кеше нет, выполняется запрос к базе данных.
        List<TopicResponseTo> topics = topicService.findByEditorId(editorId);

        // Полученный список сохраняется в Redis.
        redisCacheService.save(cacheKey, topics);

        return ResponseEntity.ok(topics);
    }

    // Создание новой темы.
    @PostMapping
    public ResponseEntity<TopicResponseTo> create(
            @Valid @RequestBody TopicRequestTo request) {

        TopicResponseTo createdTopic = topicService.save(request)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Editor with id " + request.getEditorId() + " not found"
                ));

        // После создания темы очищается кеш списков.
        redisCacheService.deleteByPattern("topics:*");

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

        // Удаляется старый кеш темы и связанных списков.
        redisCacheService.delete("topic:" + id);
        redisCacheService.deleteByPattern("topics:*");

        return ResponseEntity.ok(updatedTopic);
    }

    // Получение списка маркеров определённой темы.
    @GetMapping("/{topicId}/markers")
    public ResponseEntity<List<MarkerResponseTo>> findMarkers(
            @PathVariable Long topicId) {

        String cacheKey = "topic:" + topicId + ":markers";

        // Проверяется наличие маркеров темы в Redis.
        var cachedMarkers = redisCacheService.get(
                cacheKey,
                new TypeReference<List<MarkerResponseTo>>() {}
        );

        if (cachedMarkers.isPresent()) {
            return ResponseEntity.ok(cachedMarkers.get());
        }

        // Если данных в кеше нет, выполняется запрос к базе данных.
        List<MarkerResponseTo> markers = topicService.findMarkers(topicId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Topic with id " + topicId + " not found"
                ));

        // Полученный список маркеров сохраняется в Redis.
        redisCacheService.save(cacheKey, markers);

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

        // Очищается кеш маркеров изменённой темы.
        redisCacheService.delete("topic:" + topicId + ":markers");

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

        // Очищается кеш маркеров изменённой темы.
        redisCacheService.delete("topic:" + topicId + ":markers");

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

        // Удаляется кеш темы, её маркеров и списков тем.
        redisCacheService.delete("topic:" + id);
        redisCacheService.delete("topic:" + id + ":markers");
        redisCacheService.deleteByPattern("topics:*");

        // Удаляется кеш реакций, связанных с темой.
        redisCacheService.deleteByPattern("reactions:*");
        redisCacheService.deleteByPattern("reaction:*");

        return ResponseEntity.noContent().build();
    }

    // Нормализация фильтра для формирования одинаковых ключей кеша.
    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
