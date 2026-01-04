package com.blog.controller.v2;

import com.blog.config.SecurityUtils;
import com.blog.dto.request.TopicRequestTo;
import com.blog.dto.response.TopicResponseTo;
import com.blog.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/topics")
public class TopicControllerV2 {

    @Autowired
    private TopicService topicService;

    // Получить все топики (доступно всем аутентифицированным)
    @GetMapping
    public ResponseEntity<List<TopicResponseTo>> getAllTopics() {
        List<TopicResponseTo> topics = topicService.getAll();
        return ResponseEntity.ok(topics);
    }

    // Получить топик по ID (доступно всем аутентифицированным)
    @GetMapping("/{id}")
    public ResponseEntity<TopicResponseTo> getTopicById(@PathVariable Long id) {
        TopicResponseTo topic = topicService.getById(id);
        return ResponseEntity.ok(topic);
    }

    // Создать топик (доступно всем аутентифицированным)
    @PostMapping
    public ResponseEntity<TopicResponseTo> createTopic(@Valid @RequestBody TopicRequestTo request) {
        // Устанавливаем editorId текущего пользователя
        Long currentUserId = getCurrentUserId();
        if (currentUserId != null) {
            request.setEditorId(currentUserId);
        }

        TopicResponseTo createdTopic = topicService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTopic);
    }

    // Обновить топик
    // ADMIN может обновить любой, CUSTOMER только свой
    @PutMapping("/{id}")
    public ResponseEntity<TopicResponseTo> updateTopic(@PathVariable Long id,
                                                       @Valid @RequestBody TopicRequestTo request) {
        // Проверка аутентификации
        if (!SecurityUtils.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Проверка прав доступа
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!SecurityUtils.isAdmin() &&
                !topicService.isTopicOwner(id, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        TopicResponseTo updatedTopic = topicService.update(id, request);
        return ResponseEntity.ok(updatedTopic);
    }

    // Удалить топик
    // ADMIN может удалить любой, CUSTOMER только свой
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {

        // Проверка аутентификации
        if (!SecurityUtils.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Проверка прав доступа
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (!SecurityUtils.isAdmin() &&
                !topicService.isTopicOwner(id, currentUserId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        topicService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Получить топики по editorId (доступно всем аутентифицированным)
    @GetMapping("/editor/{editorId}")
    public ResponseEntity<Page<TopicResponseTo>> getTopicsByEditorId(
            @PathVariable Long editorId,
            @PageableDefault(size = 10, sort = "created", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TopicResponseTo> topics = topicService.getByEditorId(editorId, pageable);
        return ResponseEntity.ok(topics);
    }

    // Получить топики по tagId (доступно всем аутентифицированным)
    @GetMapping("/tag/{tagId}")
    public ResponseEntity<Page<TopicResponseTo>> getTopicsByTagId(
            @PathVariable Long tagId,
            @PageableDefault(size = 10, sort = "created", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<TopicResponseTo> topics = topicService.getByTagId(tagId, pageable);
        return ResponseEntity.ok(topics);
    }

    // Получить топики текущего пользователя
    @GetMapping("/my-topics")
    public ResponseEntity<Page<TopicResponseTo>> getMyTopics(
            @PageableDefault(size = 10, sort = "created", direction = Sort.Direction.DESC) Pageable pageable) {
        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Page<TopicResponseTo> topics = topicService.getByEditorId(currentUserId, pageable);
        return ResponseEntity.ok(topics);
    }

    // Вспомогательный метод для получения ID текущего пользователя
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            org.springframework.security.core.userdetails.UserDetails userDetails =
                    (org.springframework.security.core.userdetails.UserDetails) principal;

            if (userDetails instanceof com.blog.service.CustomUserDetails) {
                return ((com.blog.service.CustomUserDetails) userDetails).getId();
            }
        }

        return null;
    }
}