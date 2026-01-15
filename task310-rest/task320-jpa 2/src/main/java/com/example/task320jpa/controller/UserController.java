package com.example.task320jpa.controller;

import com.example.task320jpa.dto.request.UserRequestTo;
import com.example.task320jpa.dto.response.UserResponseTo;
import com.example.task320jpa.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST контроллер для работы с User
 * Поддерживает пагинацию, сортировку и управление версиями API
 * Базовый путь: /api/v1.0/users
 */
@RestController
@RequestMapping("/api/v1.0/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    
    /**
     * Создать нового пользователя
     * POST /api/v1.0/users
     * @return 201 Created + UserResponseTo
     */
    @PostMapping
    public ResponseEntity<UserResponseTo> create(@Valid @RequestBody UserRequestTo requestTo) {
        UserResponseTo response = userService.create(requestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Получить пользователя по ID
     * GET /api/v1.0/users/{id}
     * @return 200 OK + UserResponseTo
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseTo> getById(@PathVariable Long id) {
        UserResponseTo response = userService.getById(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Получить всех пользователей с пагинацией и сортировкой
     * GET /api/v1.0/users?page=0&size=10&sort=id,desc
     * @param page номер страницы (по умолчанию 0)
     * @param size размер страницы (по умолчанию 10)
     * @param sort поле и направление сортировки (например: id,desc)
     * @return 200 OK + Page<UserResponseTo>
     */
    @GetMapping
    public ResponseEntity<Page<UserResponseTo>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort) {
        
        Pageable pageable = createPageable(page, size, sort);
        Page<UserResponseTo> response = userService.getAll(pageable);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Обновить пользователя (полное обновление)
     * PUT /api/v1.0/users/{id}
     * @return 200 OK + UserResponseTo
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestTo requestTo) {
        UserResponseTo response = userService.update(id, requestTo);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Частичное обновление пользователя
     * PATCH /api/v1.0/users/{id}
     * @return 200 OK + UserResponseTo
     */
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseTo> partialUpdate(
            @PathVariable Long id,
            @RequestBody UserRequestTo requestTo) {
        UserResponseTo response = userService.partialUpdate(id, requestTo);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Удалить пользователя
     * DELETE /api/v1.0/users/{id}
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Создать Pageable из параметров запроса
     */
    private Pageable createPageable(int page, int size, String[] sort) {
        if (sort.length == 2) {
            String field = sort[0];
            Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
            return PageRequest.of(page, size, Sort.by(direction, field));
        }
        return PageRequest.of(page, size);
    }
}
