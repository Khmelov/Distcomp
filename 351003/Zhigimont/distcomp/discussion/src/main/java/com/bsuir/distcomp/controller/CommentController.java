package com.bsuir.distcomp.controller;

import com.bsuir.distcomp.dto.CommentRequestTo;
import com.bsuir.distcomp.dto.CommentResponseTo;
import com.bsuir.distcomp.entity.Comment;
import com.bsuir.distcomp.entity.CommentKey;
import com.bsuir.distcomp.mapper.CommentMapper;
import com.bsuir.distcomp.repository.CommentRepository;
import com.bsuir.distcomp.exception.EntityNotFoundException;
import com.bsuir.distcomp.service.CommentService;
import com.bsuir.types.OperationType;
import com.bsuir.types.Status;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentRepository repository;
    private final CommentService service;
    private final CommentMapper mapper;

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        log.info("📡 REST GET /comments/{} - Discussion", id);

        Comment comment = repository.findAll()
                .stream()
                .filter(c -> c.getKey().getId().equals(id))
                .findFirst()
                .orElse(null);

        if (comment == null) {
            log.warn("📡 Comment not found: id={}", id);


            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", System.currentTimeMillis());
            errorResponse.put("status", 404);
            errorResponse.put("error", "Not Found");
            errorResponse.put("message", "Comment not found with id=" + id);
            errorResponse.put("path", "/api/v1.0/comments/" + id);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        CommentResponseTo response = mapper.toDto(comment);
        response.setStatus(comment.getStatus());

        log.info("📡 Found comment: id={}, topicId={}",
                comment.getKey().getId(), comment.getKey().getTopicId());

        return ResponseEntity.ok(response);
    }


    @PutMapping
    public ResponseEntity<?> update(@RequestBody CommentRequestTo request) {
        log.info("📡 REST PUT /comments - id={}", request.getId());
        request.setOperation(OperationType.UPDATE);
        request.setCorrelationId(UUID.randomUUID().toString());
        service.process(request);
        CommentResponseTo response = new CommentResponseTo();
        response.setId(request.getId());
        response.setTopicId(request.getTopicId());
        response.setContent(request.getContent());
        response.setStatus(Status.APPROVE);

        return ResponseEntity.ok(response);
    }

    // Обработчик исключений
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(EntityNotFoundException e) {
        log.warn("Entity not found: {}", e.getMessage());
        Map<String, String> error = new HashMap<>();
        error.put("error", "Not Found");
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}