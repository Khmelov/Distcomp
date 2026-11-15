package com.example.task330.publisher.controller;

import com.example.task330.publisher.dto.MarkerRequestTo;
import com.example.task330.publisher.dto.MarkerResponseTo;
import com.example.task330.publisher.entity.Marker;
import com.example.task330.publisher.exception.ResourceNotFoundException;
import com.example.task330.publisher.service.MarkerService;
import com.example.task330.publisher.service.RedisCacheService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tools.jackson.core.type.TypeReference;

import java.util.List;

// Контроллер обрабатывает REST-запросы для работы с маркерами.
@RestController
@RequestMapping("/api/v1.0/markers")
public class MarkerController {

    // Сервис выполняет бизнес-логику работы с маркерами.
    private final MarkerService markerService;

    // Сервис обеспечивает кеширование данных в Redis.
    private final RedisCacheService redisCacheService;

    // Spring передаёт необходимые сервисы через конструктор.
    public MarkerController(
            MarkerService markerService,
            RedisCacheService redisCacheService) {
        this.markerService = markerService;
        this.redisCacheService = redisCacheService;
    }

    // Получение списка маркеров с пагинацией, сортировкой и фильтрацией.
    @GetMapping
    public ResponseEntity<List<MarkerResponseTo>> findAll(
            @PageableDefault(size = 20, sort = "id",
                    direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String name) {

        // Формируется ключ с учётом параметров запроса.
        String cacheKey = "markers:list:"
                + "page=" + pageable.getPageNumber()
                + ":size=" + pageable.getPageSize()
                + ":sort=" + pageable.getSort()
                + ":name=" + normalize(name);

        // Проверяется наличие списка маркеров в Redis.
        var cachedMarkers = redisCacheService.get(
                cacheKey,
                new TypeReference<List<MarkerResponseTo>>() {}
        );

        if (cachedMarkers.isPresent()) {
            return ResponseEntity.ok(cachedMarkers.get());
        }

        // Создаётся базовое условие, которому соответствуют все записи.
        Specification<Marker> specification =
                (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();

        // Фильтрация по названию маркера.
        if (name != null && !name.isBlank()) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("name")),
                            "%" + name.trim().toLowerCase() + "%"
                    ));
        }

        // Из страницы извлекается список маркеров для JSON-массива.
        Page<MarkerResponseTo> page =
                markerService.findAll(specification, pageable);

        List<MarkerResponseTo> markers = page.getContent();

        // Полученный список сохраняется в Redis.
        redisCacheService.save(cacheKey, markers);

        return ResponseEntity.ok(markers);
    }

    // Получение маркера по идентификатору.
    @GetMapping("/{id}")
    public ResponseEntity<MarkerResponseTo> findById(@PathVariable Long id) {

        String cacheKey = "marker:" + id;

        // Проверяется наличие маркера в Redis.
        var cachedMarker = redisCacheService.get(
                cacheKey,
                MarkerResponseTo.class
        );

        if (cachedMarker.isPresent()) {
            return ResponseEntity.ok(cachedMarker.get());
        }

        // Если данных в кеше нет, выполняется запрос к базе данных.
        MarkerResponseTo marker = markerService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Marker with id " + id + " not found"
                ));

        // Полученный маркер сохраняется в Redis.
        redisCacheService.save(cacheKey, marker);

        return ResponseEntity.ok(marker);
    }

    // Создание нового маркера.
    @PostMapping
    public ResponseEntity<MarkerResponseTo> create(
            @Valid @RequestBody MarkerRequestTo request) {

        MarkerResponseTo createdMarker = markerService.save(request);

        // После создания маркера очищается кеш списков.
        redisCacheService.deleteByPattern("markers:list:*");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdMarker);
    }

    // Обновление существующего маркера.
    @PutMapping("/{id}")
    public ResponseEntity<MarkerResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody MarkerRequestTo request) {

        MarkerResponseTo updatedMarker = markerService.update(id, request)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Marker with id " + id + " not found"
                ));

        // Удаляется кеш маркера и списков маркеров.
        redisCacheService.delete("marker:" + id);
        redisCacheService.deleteByPattern("markers:list:*");

        // Очищается кеш маркеров, связанных с темами.
        redisCacheService.deleteByPattern("topic:*:markers");

        return ResponseEntity.ok(updatedMarker);
    }

    // Удаление маркера по идентификатору.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        if (!markerService.deleteById(id)) {
            throw new ResourceNotFoundException(
                    "Marker with id " + id + " not found"
            );
        }

        // Удаляется кеш маркера и списков маркеров.
        redisCacheService.delete("marker:" + id);
        redisCacheService.deleteByPattern("markers:list:*");

        // Очищается кеш маркеров, связанных с темами.
        redisCacheService.deleteByPattern("topic:*:markers");

        return ResponseEntity.noContent().build();
    }

    // Нормализация фильтра для формирования одинаковых ключей кеша.
    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
