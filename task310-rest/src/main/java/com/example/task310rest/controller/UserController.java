package com.example.task310rest.controller;

import com.example.task310rest.dto.request.UserRequestTo;
import com.example.task310rest.dto.response.UserResponseTo;
import com.example.task310rest.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для работы с User
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
     * Получить всех пользователей
     * GET /api/v1.0/users
     * @return 200 OK + List<UserResponseTo>
     */
    @GetMapping
    public ResponseEntity<List<UserResponseTo>> getAll() {
        List<UserResponseTo> response = userService.getAll();
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
}
