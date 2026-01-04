package com.blog.controller.v2;

import com.blog.config.SecurityUtils;
import com.blog.dto.request.EditorRequestTo;
import com.blog.dto.response.EditorResponseTo;
import com.blog.service.EditorService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/editors")
public class EditorControllerV2 {

    private static final Logger logger = LoggerFactory.getLogger(EditorControllerV2.class);

    @Autowired
    private EditorService editorService;

    // Получить всех редакторов (только для ADMIN)
    @GetMapping
    public ResponseEntity<List<EditorResponseTo>> getAllEditors() {
        logger.debug("GET /api/v2.0/editors - Checking authentication...");

        // Проверяем аутентификацию
        if (!SecurityUtils.isAuthenticated()) {
            logger.debug("GET /api/v2.0/editors - User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        logger.debug("GET /api/v2.0/editors - User authenticated: {}",
                SecurityUtils.getCurrentUserLogin());

        // Проверяем роль ADMIN
        if (!SecurityUtils.isAdmin()) {
            logger.debug("GET /api/v2.0/editors - User is not ADMIN: {}",
                    SecurityUtils.getCurrentUserRoles());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<EditorResponseTo> editors = editorService.getAll();
        return ResponseEntity.ok(editors);
    }

    // Получить текущего пользователя
    @GetMapping("/me")
    public ResponseEntity<EditorResponseTo> getCurrentEditor() {
        logger.debug("GET /api/v2.0/editors/me - Checking authentication...");

        // Проверяем аутентификацию
        if (!SecurityUtils.isAuthenticated()) {
            logger.debug("GET /api/v2.0/editors/me - User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        logger.debug("GET /api/v2.0/editors/me - Current user ID: {}", currentUserId);

        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return getEditorById(currentUserId);
    }

    // Получить редактора по ID
    // ADMIN может получить любого, CUSTOMER только себя
    @GetMapping("/{id}")
    public ResponseEntity<EditorResponseTo> getEditorById(@PathVariable Long id) {
        logger.debug("GET /api/v2.0/editors/{} - Checking authentication...", id);

        // Проверяем аутентификацию
        if (!SecurityUtils.isAuthenticated()) {
            logger.debug("GET /api/v2.0/editors/{} - User not authenticated", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        logger.debug("GET /api/v2.0/editors/{} - Current user ID: {}, Requested ID: {}",
                id, currentUserId, id);

        // ADMIN может получить любого пользователя
        // CUSTOMER может получить только себя
        if (!SecurityUtils.isAdmin() && (currentUserId == null || !currentUserId.equals(id))) {
            logger.debug("GET /api/v2.0/editors/{} - Access denied: Not ADMIN and not owner", id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        EditorResponseTo editor = editorService.getById(id);
        return ResponseEntity.ok(editor);
    }

    // Создание редактора уже доступно без аутентификации через AuthControllerV2
    // Этот endpoint оставляем только для ADMIN (на случай если нужно создать изнутри системы)
    @PostMapping("/admin-create")
    public ResponseEntity<EditorResponseTo> createEditorByAdmin(@Valid @RequestBody EditorRequestTo request) {
        logger.debug("POST /api/v2.0/editors/admin-create - Checking authentication...");

        // Проверяем аутентификацию и роль
        if (!SecurityUtils.isAuthenticated()) {
            logger.debug("POST /api/v2.0/editors/admin-create - User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!SecurityUtils.isAdmin()) {
            logger.debug("POST /api/v2.0/editors/admin-create - User is not ADMIN");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        EditorResponseTo createdEditor = editorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEditor);
    }

    // Обновление редактора
    // ADMIN может обновить любого, CUSTOMER только себя
    @PutMapping("/{id}")
    public ResponseEntity<EditorResponseTo> updateEditor(@PathVariable Long id,
                                                         @Valid @RequestBody EditorRequestTo request) {
        logger.debug("PUT /api/v2.0/editors/{} - Checking authentication...", id);

        // Проверяем аутентификацию
        if (!SecurityUtils.isAuthenticated()) {
            logger.debug("PUT /api/v2.0/editors/{} - User not authenticated", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long currentUserId = SecurityUtils.getCurrentUserId();
        logger.debug("PUT /api/v2.0/editors/{} - Current user ID: {}, Requested ID: {}",
                id, currentUserId, id);

        // Проверяем права доступа
        if (!SecurityUtils.isAdmin() && (currentUserId == null || !currentUserId.equals(id))) {
            logger.debug("PUT /api/v2.0/editors/{} - Access denied: Not ADMIN and not owner", id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // CUSTOMER не может изменить свою роль
        if (!SecurityUtils.isAdmin() && request.getRole() != null) {
            request.setRole(null); // Игнорируем попытку изменить роль
        }

        EditorResponseTo updatedEditor = editorService.update(id, request);
        return ResponseEntity.ok(updatedEditor);
    }

    // Удаление редактора
    // ADMIN может удалить любого, CUSTOMER может удалить только себя
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEditor(@PathVariable Long id) {
        logger.debug("DELETE /api/v2.0/editors/{}", id);

        try {
            // Пробуем получить текущего пользователя
            Long currentUserId = SecurityUtils.getCurrentUserId();
            logger.debug("Current user ID: {}", currentUserId);

            // Если не можем получить ID пользователя, значит не аутентифицирован
            if (currentUserId == null) {
                logger.debug("User not authenticated or invalid token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // ADMIN может удалить любого
            // CUSTOMER может удалить только себя
            if (!SecurityUtils.isAdmin() && !currentUserId.equals(id)) {
                logger.debug("Access denied: Not ADMIN and not owner");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            editorService.delete(id);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            logger.error("Error in deleteEditor: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}