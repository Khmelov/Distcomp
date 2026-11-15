package com.example.task330.publisher.controller;

import com.example.task330.publisher.dto.EditorRequestTo;
import com.example.task330.publisher.dto.EditorResponseTo;
import com.example.task330.publisher.entity.Editor;
import com.example.task330.publisher.exception.ResourceNotFoundException;
import com.example.task330.publisher.service.EditorService;
import com.example.task330.publisher.service.RedisCacheService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tools.jackson.core.type.TypeReference;

import java.util.List;

// Контроллер обрабатывает REST-запросы для работы с редакторами.
// Все запросы начинаются с адреса /api/v1.0/editors.
@RestController
@RequestMapping("/api/v1.0/editors")
public class EditorController {

    // Сервис выполняет основную логику работы с редакторами.
    private final EditorService editorService;

    // Сервис обеспечивает кеширование данных в Redis.
    private final RedisCacheService redisCacheService;

    // Spring передаёт необходимые сервисы через конструктор.
    public EditorController(
            EditorService editorService,
            RedisCacheService redisCacheService) {
        this.editorService = editorService;
        this.redisCacheService = redisCacheService;
    }

    // Получение списка редакторов с пагинацией, сортировкой и фильтрацией.
    @GetMapping
    public ResponseEntity<List<EditorResponseTo>> findAll(
            Pageable pageable,
            @RequestParam(required = false) String login,
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname) {

        // Формируется ключ с учётом параметров запроса.
        String cacheKey = "editors:list:"
                + "page=" + pageable.getPageNumber()
                + ":size=" + pageable.getPageSize()
                + ":sort=" + pageable.getSort()
                + ":login=" + normalize(login)
                + ":firstname=" + normalize(firstname)
                + ":lastname=" + normalize(lastname);

        // Проверяется наличие списка редакторов в Redis.
        var cachedEditors = redisCacheService.get(
                cacheKey,
                new TypeReference<List<EditorResponseTo>>() {}
        );

        if (cachedEditors.isPresent()) {
            return ResponseEntity.ok(cachedEditors.get());
        }

        // Создаётся базовое условие, которому соответствуют все записи.
        Specification<Editor> specification =
                (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        // Фильтрация по логину.
        if (login != null && !login.isBlank()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("login")),
                            "%" + login.trim().toLowerCase() + "%"
                    ));
        }

        // Фильтрация по имени.
        if (firstname != null && !firstname.isBlank()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("firstname")),
                            "%" + firstname.trim().toLowerCase() + "%"
                    ));
        }

        // Фильтрация по фамилии.
        if (lastname != null && !lastname.isBlank()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("lastname")),
                            "%" + lastname.trim().toLowerCase() + "%"
                    ));
        }

        // Из страницы извлекается список редакторов для JSON-массива.
        Page<EditorResponseTo> page =
                editorService.findAll(specification, pageable);

        List<EditorResponseTo> editors = page.getContent();

        // Полученный список сохраняется в Redis.
        redisCacheService.save(cacheKey, editors);

        return ResponseEntity.ok(editors);
    }

    // Получение редактора по идентификатору.
    // Сначала выполняется поиск в Redis, затем в базе данных.
    @GetMapping("/{id}")
    public ResponseEntity<EditorResponseTo> findById(@PathVariable Long id) {

        String cacheKey = "editor:" + id;

        // Проверяется наличие редактора в Redis.
        var cachedEditor = redisCacheService.get(
                cacheKey,
                EditorResponseTo.class
        );

        if (cachedEditor.isPresent()) {
            return ResponseEntity.ok(cachedEditor.get());
        }

        // Если данных в кеше нет, выполняется запрос к базе данных.
        EditorResponseTo editor = editorService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Editor with id " + id + " not found"
                ));

        // Полученный редактор сохраняется в Redis.
        redisCacheService.save(cacheKey, editor);

        return ResponseEntity.ok(editor);
    }

    // Создание нового редактора.
    // @Valid проверяет ограничения DTO перед передачей данных в сервис.
    // При успешном создании возвращается HTTP-код 201 Created.
    @PostMapping
    public ResponseEntity<EditorResponseTo> create(
            @Valid @RequestBody EditorRequestTo request) {

        EditorResponseTo createdEditor = editorService.save(request);

        // После создания редактора очищается кеш списков.
        redisCacheService.deleteByPattern("editors:list:*");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdEditor);
    }

    // Обновление существующего редактора.
    // Перед изменением проверяется наличие записи.
    @PutMapping("/{id}")
    public ResponseEntity<EditorResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody EditorRequestTo request) {

        if (editorService.findById(id).isEmpty()) {
            throw new ResourceNotFoundException(
                    "Editor with id " + id + " not found"
            );
        }

        EditorResponseTo updatedEditor = editorService.update(id, request);

        // Удаляется старый кеш редактора и списков.
        redisCacheService.delete("editor:" + id);
        redisCacheService.deleteByPattern("editors:list:*");

        return ResponseEntity.ok(updatedEditor);
    }

    // Удаление редактора по идентификатору.
    // Если запись отсутствует, возвращается ошибка 404.
    // При успешном удалении возвращается HTTP-код 204 No Content.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        if (!editorService.deleteById(id)) {
            throw new ResourceNotFoundException(
                    "Editor with id " + id + " not found"
            );
        }

        // Удаляется кеш редактора и всех списков редакторов.
        redisCacheService.delete("editor:" + id);
        redisCacheService.deleteByPattern("editors:list:*");

        // Удаление редактора также затрагивает связанные темы и реакции.
        redisCacheService.deleteByPattern("topics:*");
        redisCacheService.deleteByPattern("topic:*");
        redisCacheService.deleteByPattern("reactions:*");
        redisCacheService.deleteByPattern("reaction:*");

        return ResponseEntity.noContent().build();
    }

    // Нормализация фильтра для формирования одинаковых ключей кеша.
    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
