package com.task.rest.controller;

import com.task.rest.dto.NoticeRequestTo;
import com.task.rest.dto.NoticeResponseTo;
import com.task.rest.service.NoticeService;
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

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<NoticeResponseTo> create(@Valid @RequestBody NoticeRequestTo request) {
        log.info("POST /api/v1.0/notices - Creating notice");
        NoticeResponseTo response = noticeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<NoticeResponseTo>> getAll() {
        log.info("GET /api/v1.0/notices - Getting all notices");
        return ResponseEntity.ok(noticeService.getAll());
    }

    // Новый endpoint: GET /notices/{id}
    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponseTo> getByIdOnly(@PathVariable Long id) {
        log.info("GET /api/v1.0/notices/{}", id);
        NoticeResponseTo response = noticeService.getByIdOnly(id);
        return response != null
                ? ResponseEntity.ok(response)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/{country}/{tweetId}/{id}")
    public ResponseEntity<NoticeResponseTo> getById(
            @PathVariable String country,
            @PathVariable Long tweetId,
            @PathVariable Long id) {
        log.info("GET /api/v1.0/notices/{}/{}/{}", country, tweetId, id);
        NoticeResponseTo response = noticeService.getById(country, tweetId, id);
        return response != null
                ? ResponseEntity.ok(response)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<NoticeResponseTo>> getByTweetId(@PathVariable Long tweetId) {
        log.info("GET /api/v1.0/notices/tweet/{}", tweetId);
        return ResponseEntity.ok(noticeService.getByTweetId(tweetId));
    }

    // Новый endpoint: PUT /notices/{id}
    @PutMapping("/{id}")
    public ResponseEntity<NoticeResponseTo> updateByIdOnly(
            @PathVariable Long id,
            @Valid @RequestBody NoticeRequestTo request) {
        log.info("PUT /api/v1.0/notices/{}", id);
        NoticeResponseTo response = noticeService.updateByIdOnly(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{country}/{tweetId}/{id}")
    public ResponseEntity<NoticeResponseTo> update(
            @PathVariable String country,
            @PathVariable Long tweetId,
            @PathVariable Long id,
            @Valid @RequestBody NoticeRequestTo request) {
        log.info("PUT /api/v1.0/notices/{}/{}/{}", country, tweetId, id);
        NoticeResponseTo response = noticeService.update(country, tweetId, id, request);
        return ResponseEntity.ok(response);
    }

    // Новый endpoint: DELETE /notices/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByIdOnly(@PathVariable Long id) {
        log.info("DELETE /api/v1.0/notices/{}", id);
        noticeService.deleteByIdOnly(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{country}/{tweetId}/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String country,
            @PathVariable Long tweetId,
            @PathVariable Long id) {
        log.info("DELETE /api/v1.0/notices/{}/{}/{}", country, tweetId, id);
        noticeService.delete(country, tweetId, id);
        return ResponseEntity.noContent().build();
    }
}
