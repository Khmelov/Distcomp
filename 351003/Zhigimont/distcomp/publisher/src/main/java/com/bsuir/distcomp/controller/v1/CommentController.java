package com.bsuir.distcomp.controller.v1;

import com.bsuir.distcomp.dto.CommentListResponseTo;
import com.bsuir.distcomp.dto.CommentRequestTo;
import com.bsuir.distcomp.dto.CommentResponseTo;
import com.bsuir.distcomp.kafka.CommentProducer;
import com.bsuir.distcomp.service.ResponseHolder;
import com.bsuir.types.OperationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestController
@RequestMapping("/api/v1.0/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentProducer producer;
    private final ResponseHolder holder;

    @GetMapping
    @Cacheable(value = "comments", key = "'all'")
    public ResponseEntity<?> getAll() throws Exception {
        log.info("🟢 GET /comments - fetching from Discussion (not cached)");

        String correlationId = UUID.randomUUID().toString();
        CommentRequestTo request = new CommentRequestTo();
        request.setOperation(OperationType.GET_ALL);
        request.setCorrelationId(correlationId);

        CompletableFuture<CommentListResponseTo> future = holder.createList(correlationId);
        producer.send(request);

        try {
            CommentListResponseTo response = future.get(10, TimeUnit.SECONDS);
            List<CommentResponseTo> comments = response.getComments();
            if (comments == null) comments = List.of();

            log.info("✅ GET /comments - returned {} comments", comments.size());
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            log.error("🔴 GET /comments error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    @Cacheable(value = "comments", key = "#id")
    public CommentResponseTo getById(@PathVariable Long id) throws Exception {
        log.info("🟢 GET /comments/{} - fetching from Discussion (not cached)", id);

        String correlationId = UUID.randomUUID().toString();
        CommentRequestTo request = new CommentRequestTo();
        request.setId(id);
        request.setOperation(OperationType.GET_BY_ID);
        request.setCorrelationId(correlationId);

        CompletableFuture<CommentResponseTo> future = holder.createSingle(correlationId);
        producer.send(request);

        try {
            CommentResponseTo response = future.get(10, TimeUnit.SECONDS);
            log.info("✅ GET /comments/{} - found", id);
            return response;
        } catch (Exception e) {
            log.error("🔴 GET /comments/{} error: {}", id, e.getMessage());
            return null;
        }
    }

    @PostMapping
    @CacheEvict(value = "comments", allEntries = true)
    public ResponseEntity<?> create(@RequestBody CommentRequestTo dto) throws Exception {
        String correlationId = UUID.randomUUID().toString();
        dto.setOperation(OperationType.CREATE);
        dto.setCorrelationId(correlationId);

        log.info("🟢 POST /comments - topicId={}", dto.getTopicId());

        CompletableFuture<CommentResponseTo> future = holder.createSingle(correlationId);
        producer.send(dto);

        try {
            CommentResponseTo response = future.get(10, TimeUnit.SECONDS);
            log.info("✅ Comment created: id={}, status={}", response.getId(), response.getStatus());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (TimeoutException e) {
            log.error("🔴 Timeout waiting for moderation result");
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        }
    }

    @PutMapping
    @CacheEvict(value = "comments", allEntries = true)
    public ResponseEntity<?> update(@RequestBody CommentRequestTo dto) throws Exception {
        log.info("🟢 PUT /comments - id={}, topicId={}", dto.getId(), dto.getTopicId());

        String correlationId = UUID.randomUUID().toString();
        dto.setOperation(OperationType.UPDATE);
        dto.setCorrelationId(correlationId);

        CompletableFuture<CommentResponseTo> future = holder.createSingle(correlationId);
        producer.send(dto);

        try {
            CommentResponseTo response = future.get(10, TimeUnit.SECONDS);
            log.info("✅ PUT completed: id={}, status={}", response.getId(), response.getStatus());
            return ResponseEntity.ok(response);
        } catch (TimeoutException e) {
            log.error("🔴 Timeout on PUT");
            return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
        }
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = "comments", allEntries = true)
    public ResponseEntity<?> delete(@PathVariable Long id) throws Exception {
        String correlationId = UUID.randomUUID().toString();

        CommentRequestTo dto = new CommentRequestTo();
        dto.setId(id);
        dto.setOperation(OperationType.DELETE);
        dto.setCorrelationId(correlationId);

        log.info("🟢 DELETE /comments/{}", id);

        CompletableFuture<CommentResponseTo> future = holder.createSingle(correlationId);
        producer.send(dto);

        try {
            future.get(10, TimeUnit.SECONDS);
            log.info("✅ DELETE /comments/{} completed", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("🔴 Error deleting: {}", e.getMessage());
            return ResponseEntity.noContent().build();
        }
    }
}