package com.example.task310rest.controller;

import com.example.task310rest.dto.request.MarkRequestTo;
import com.example.task310rest.dto.response.MarkResponseTo;
import com.example.task310rest.service.MarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для работы с Mark
 * Базовый путь: /api/v1.0/marks
 */
@RestController
@RequestMapping("/api/v1.0/marks")
@RequiredArgsConstructor
public class MarkController {
    
    private final MarkService markService;
    
    /**
     * Создать новую метку
     * POST /api/v1.0/marks
     * @return 201 Created + MarkResponseTo
     */
    @PostMapping
    public ResponseEntity<MarkResponseTo> create(@Valid @RequestBody MarkRequestTo requestTo) {
        MarkResponseTo response = markService.create(requestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Получить метку по ID
     * GET /api/v1.0/marks/{id}
     * @return 200 OK + MarkResponseTo
     */
    @GetMapping("/{id}")
    public ResponseEntity<MarkResponseTo> getById(@PathVariable Long id) {
        MarkResponseTo response = markService.getById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получить все метки
     * GET /api/v1.0/marks
     * @return 200 OK + List<MarkResponseTo>
     */
    @GetMapping
    public ResponseEntity<List<MarkResponseTo>> getAll() {
        List<MarkResponseTo> response = markService.getAll();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Обновить метку (полное обновление)
     * PUT /api/v1.0/marks/{id}
     * @return 200 OK + MarkResponseTo
     */
    @PutMapping("/{id}")
    public ResponseEntity<MarkResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody MarkRequestTo requestTo) {
        MarkResponseTo response = markService.update(id, requestTo);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Частичное обновление метки
     * PATCH /api/v1.0/marks/{id}
     * @return 200 OK + MarkResponseTo
     */
    @PatchMapping("/{id}")
    public ResponseEntity<MarkResponseTo> partialUpdate(
            @PathVariable Long id,
            @RequestBody MarkRequestTo requestTo) {
        MarkResponseTo response = markService.partialUpdate(id, requestTo);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Удалить метку
     * DELETE /api/v1.0/marks/{id}
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        markService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
