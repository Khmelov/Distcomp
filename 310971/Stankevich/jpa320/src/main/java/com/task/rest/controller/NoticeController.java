package com.task.rest.controller;

import com.task.rest.dto.NoticeRequestTo;
import com.task.rest.dto.NoticeResponseTo;
import com.task.rest.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1.0/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponseTo> getById(@PathVariable Long id) {
        log.info("GET request for notice with id: {}", id);
        return ResponseEntity.ok(noticeService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<NoticeResponseTo>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("GET request for all notices");
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(noticeService.getAll(pageable).getContent());
    }

    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<NoticeResponseTo>> getByTweetId(@PathVariable Long tweetId) {
        log.info("GET request for notices by tweet id: {}", tweetId);
        return ResponseEntity.ok(noticeService.getByTweetId(tweetId));
    }

    @PostMapping
    public ResponseEntity<NoticeResponseTo> create(@Valid @RequestBody NoticeRequestTo requestTo) {
        log.info("POST request to create notice");
        return ResponseEntity.status(HttpStatus.CREATED).body(noticeService.create(requestTo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoticeResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody NoticeRequestTo requestTo) {
        log.info("PUT request to update notice with id: {}", id);
        return ResponseEntity.ok(noticeService.update(id, requestTo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("DELETE request for notice with id: {}", id);
        noticeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
