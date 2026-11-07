
package com.example.task310.controller;

import com.example.task310.dto.EditorRequestTo;
import com.example.task310.dto.EditorResponseTo;
import com.example.task310.exception.ResourceNotFoundException;
import com.example.task310.service.EditorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Контроллер обрабатывает REST-запросы для работы с редакторами.
// Все запросы начинаются с адреса /api/v1.0/editors.
@RestController
@RequestMapping("/api/v1.0/editors")
public class EditorController {

    // Сервис выполняет основную логику работы с редакторами.
    private final EditorService editorService;

    // Получение сервиса через конструктор (внедрение зависимости Spring).
    public EditorController(EditorService editorService) {
        this.editorService = editorService;
    }

    // Получение полного списка редакторов.
    // Вызывает EditorService.findAll(), получает список EditorResponseTo
    // и возвращает его клиенту с HTTP-кодом 200 OK.
    @GetMapping
    public ResponseEntity<List<EditorResponseTo>> findAll() {
        return ResponseEntity.ok(editorService.findAll());
    }

    // Получение редактора по его идентификатору.
    // Сервис ищет редактора и возвращает Optional с DTO EditorResponseTo.
    // Если запись отсутствует, выбрасывается ResourceNotFoundException,
    // которую обрабатывает GlobalExceptionHandler с кодом 404.
    @GetMapping("/{id}")
    public ResponseEntity<EditorResponseTo> findById(@PathVariable Long id) {
        EditorResponseTo editor = editorService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Editor with id " + id + " not found"
                ));

        return ResponseEntity.ok(editor);
    }

    // Создание нового редактора.
    // Клиент передаёт JSON, который преобразуется в EditorRequestTo.
    // Аннотация @Valid запускает проверку ограничений DTO.
    // EditorService проверяет уникальность логина, создаёт модель Editor,
    // сохраняет её через EditorRepository и формирует EditorResponseTo.
    // В ответ возвращается созданный редактор с HTTP-кодом 201 Created.
    @PostMapping
    public ResponseEntity<EditorResponseTo> create(
            @Valid @RequestBody EditorRequestTo request) {

        EditorResponseTo createdEditor = editorService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdEditor);
    }

    // Изменение существующего редактора.
    // Сначала проверяется наличие редактора по идентификатору.
    // Если запись отсутствует, выбрасывается ResourceNotFoundException.
    // Затем EditorService.update() обновляет данные и возвращает DTO.
    // Результат отправляется клиенту с HTTP-кодом 200 OK.
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

        return ResponseEntity.ok(updatedEditor);
    }

    // Удаление редактора по идентификатору.
    // EditorService.deleteById() пытается удалить запись через Repository.
    // Если редактор не найден, выбрасывается ResourceNotFoundException.
    // При успешном удалении возвращается HTTP-код 204 No Content без тела ответа.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!editorService.deleteById(id)) {
            throw new ResourceNotFoundException(
                    "Editor with id " + id + " not found"
            );
        }

        return ResponseEntity.noContent().build();
    }
}
