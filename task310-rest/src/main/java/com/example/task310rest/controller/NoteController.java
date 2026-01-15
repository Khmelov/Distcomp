package com.example.task310rest.controller;

import com.example.task310rest.dto.request.NoteRequestTo;
import com.example.task310rest.dto.response.NoteResponseTo;
import com.example.task310rest.service.NoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для работы с Note
 * Базовый путь: /api/v1.0/notes
 */
@RestController
@RequestMapping("/api/v1.0/notes")
@RequiredArgsConstructor
public class NoteController {
    
    private final NoteService noteService;
    
    /**
     * Создать новую заметку
     * POST /api/v1.0/notes
     * @return 201 Created + NoteResponseTo
     */
    @PostMapping
    public ResponseEntity<NoteResponseTo> create(@Valid @RequestBody NoteRequestTo requestTo) {
        NoteResponseTo response = noteService.create(requestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Получить заметку по ID
     * GET /api/v1.0/notes/{id}
     * @return 200 OK + NoteResponseTo
     */
    @GetMapping("/{id}")
    public ResponseEntity<NoteResponseTo> getById(@PathVariable Long id) {
        NoteResponseTo response = noteService.getById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получить все заметки
     * GET /api/v1.0/notes
     * @return 200 OK + List<NoteResponseTo>
     */
    @GetMapping
    public ResponseEntity<List<NoteResponseTo>> getAll() {
        List<NoteResponseTo> response = noteService.getAll();
        return ResponseEntity.ok(response);
    }
    
    /**
     * Обновить заметку (полное обновление)
     * PUT /api/v1.0/notes/{id}
     * @return 200 OK + NoteResponseTo
     */
    @PutMapping("/{id}")
    public ResponseEntity<NoteResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody NoteRequestTo requestTo) {
        NoteResponseTo response = noteService.update(id, requestTo);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Частичное обновление заметки
     * PATCH /api/v1.0/notes/{id}
     * @return 200 OK + NoteResponseTo
     */
    @PatchMapping("/{id}")
    public ResponseEntity<NoteResponseTo> partialUpdate(
            @PathVariable Long id,
            @RequestBody NoteRequestTo requestTo) {
        NoteResponseTo response = noteService.partialUpdate(id, requestTo);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Удалить заметку
     * DELETE /api/v1.0/notes/{id}
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
