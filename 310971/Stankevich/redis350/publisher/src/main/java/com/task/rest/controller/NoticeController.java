package com.task.rest.controller;

import com.task.rest.dto.NoticeRequestTo;
import com.task.rest.dto.NoticeResponseTo;
import com.task.rest.service.kafka.NoticeKafkaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeKafkaService noticeKafkaService;

    @PostMapping
    public ResponseEntity<NoticeResponseTo> create(@Valid @RequestBody NoticeRequestTo request) {
        log.info("POST /api/v1.0/notices - Creating notice via Kafka");
        NoticeResponseTo response = noticeKafkaService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<NoticeResponseTo>> getAll() {
        log.info("GET /api/v1.0/notices - Getting all notices via Kafka");
        return ResponseEntity.ok(noticeKafkaService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponseTo> getById(@PathVariable Long id) {
        log.info("GET /api/v1.0/notices/{} via Kafka", id);
        NoticeResponseTo response = noticeKafkaService.getByIdOnly(id);
        return response != null
                ? ResponseEntity.ok(response)
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoticeResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody NoticeRequestTo request) {
        log.info("PUT /api/v1.0/notices/{} via Kafka", id);
        NoticeResponseTo response = noticeKafkaService.updateByIdOnly(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE /api/v1.0/notices/{} via Kafka", id);
        noticeKafkaService.deleteByIdOnly(id);
        return ResponseEntity.noContent().build();
    }
}
