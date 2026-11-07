
package com.example.task320.controller;

import com.example.task320.dto.EditorRequestTo;
import com.example.task320.dto.EditorResponseTo;
import com.example.task320.entity.Editor;
import com.example.task320.exception.ResourceNotFoundException;
import com.example.task320.service.EditorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    // Spring передаёт сервис через конструктор.
    public EditorController(EditorService editorService) {
        this.editorService = editorService;
    }

    // Получение списка редакторов с пагинацией, сортировкой и фильтрацией.
    @GetMapping
    public ResponseEntity<List<EditorResponseTo>> findAll(
            Pageable pageable,
            @RequestParam(required = false) String login,
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname) {

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

        return ResponseEntity.ok(page.getContent());
    }

    // Получение редактора по идентификатору.
    // Если запись отсутствует, выбрасывается ResourceNotFoundException.
    @GetMapping("/{id}")
    public ResponseEntity<EditorResponseTo> findById(@PathVariable Long id) {
        EditorResponseTo editor = editorService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Editor with id " + id + " not found"
                ));

        return ResponseEntity.ok(editor);
    }

    // Создание нового редактора.
    // @Valid проверяет ограничения DTO перед передачей данных в сервис.
    // При успешном создании возвращается HTTP-код 201 Created.
    @PostMapping
    public ResponseEntity<EditorResponseTo> create(
            @Valid @RequestBody EditorRequestTo request) {

        EditorResponseTo createdEditor = editorService.save(request);

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

        return ResponseEntity.noContent().build();
    }
}
