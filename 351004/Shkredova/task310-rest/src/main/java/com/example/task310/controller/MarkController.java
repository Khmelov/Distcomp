package com.example.task310.controller;

import com.example.task310.dto.MarkRequestTo;
import com.example.task310.dto.MarkResponseTo;
import com.example.task310.service.MarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1.0/marks")
@RequiredArgsConstructor
public class MarkController {

    private final MarkService markService;

    // ==================== CRUD ОСНОВНЫЕ ====================

    @PostMapping
    public ResponseEntity<MarkResponseTo> create(@Valid @RequestBody MarkRequestTo request) {
        MarkResponseTo response = markService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MarkResponseTo>> findAll() {
        return ResponseEntity.ok(markService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkResponseTo> findById(@PathVariable Long id) {
        return ResponseEntity.ok(markService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarkResponseTo> update(@PathVariable Long id,
                                                 @Valid @RequestBody MarkRequestTo request) {
        return ResponseEntity.ok(markService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        markService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ==================== ТЕСТОВЫЕ МЕТОДЫ ====================

    /**
     * Создать метку, если её нет (для тестов)
     * POST /api/v1.0/marks/ensure/{name}
     */
    @PostMapping("/ensure/{name}")
    public ResponseEntity<String> createMarkIfNotExists(@PathVariable String name) {
        markService.createMarkIfNotExists(name);
        return ResponseEntity.ok("Метка создана (или уже существовала): " + name);
    }

    /**
     * Создать три метки redX, greenX, blueX для заданного ID
     * POST /api/v1.0/marks/create-for-id/{id}
     */
    @PostMapping("/create-for-id/{id}")
    public ResponseEntity<String> createTestMarksForId(@PathVariable String id) {
        String result = markService.createTestMarksForId(id);
        return ResponseEntity.ok(result);
    }

    /**
     * Проверить, существуют ли три метки для заданного ID
     * GET /api/v1.0/marks/check-for-id/{id}
     */
    @GetMapping("/check-for-id/{id}")
    public ResponseEntity<Boolean> checkTestMarksForId(@PathVariable String id) {
        boolean exists = markService.checkTestMarksForId(id);
        return ResponseEntity.ok(exists);
    }

    /**
     * Удалить три метки для заданного ID
     * DELETE /api/v1.0/marks/delete-for-id/{id}
     */
    @DeleteMapping("/delete-for-id/{id}")
    public ResponseEntity<String> deleteTestMarksForId(@PathVariable String id) {
        String result = markService.deleteTestMarksForId(id);
        return ResponseEntity.ok(result);
    }

    /**
     * Проверить существование метки по имени
     * GET /api/v1.0/marks/name/{name}
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Boolean> existsByName(@PathVariable String name) {
        return ResponseEntity.ok(markService.findByName(name).isPresent());
    }
}