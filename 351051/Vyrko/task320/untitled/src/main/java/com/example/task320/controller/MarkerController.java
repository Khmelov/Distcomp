
package com.example.task320.controller;

import com.example.task320.dto.MarkerRequestTo;
import com.example.task320.dto.MarkerResponseTo;
import com.example.task320.entity.Marker;
import com.example.task320.exception.ResourceNotFoundException;
import com.example.task320.service.MarkerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Контроллер обрабатывает REST-запросы для работы с маркерами.
@RestController
@RequestMapping("/api/v1.0/markers")
public class MarkerController {

    private final MarkerService markerService;

    // Spring передаёт сервис через конструктор.
    public MarkerController(MarkerService markerService) {
        this.markerService = markerService;
    }

    // Получение списка маркеров с пагинацией, сортировкой и фильтрацией.
    @GetMapping
    public ResponseEntity<List<MarkerResponseTo>> findAll(
            Pageable pageable,
            @RequestParam(required = false) String name) {

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

        return ResponseEntity.ok(page.getContent());
    }

    // Получение маркера по идентификатору.
    @GetMapping("/{id}")
    public ResponseEntity<MarkerResponseTo> findById(@PathVariable Long id) {
        MarkerResponseTo marker = markerService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Marker with id " + id + " not found"
                ));

        return ResponseEntity.ok(marker);
    }

    // Создание нового маркера.
    @PostMapping
    public ResponseEntity<MarkerResponseTo> create(
            @Valid @RequestBody MarkerRequestTo request) {

        MarkerResponseTo createdMarker = markerService.save(request);

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

        return ResponseEntity.noContent().build();
    }
}
