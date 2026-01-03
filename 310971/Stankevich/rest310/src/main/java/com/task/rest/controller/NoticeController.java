package com.task.rest.controller;

import com.task.rest.dto.NoticeRequestTo;
import com.task.rest.dto.NoticeResponseTo;
import com.task.rest.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService service;

    @PostMapping
    public ResponseEntity<NoticeResponseTo> create(@Valid @RequestBody NoticeRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<NoticeResponseTo>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping
    public ResponseEntity<NoticeResponseTo> update(@Valid @RequestBody NoticeRequestTo request) {
        return ResponseEntity.ok(service.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<NoticeResponseTo>> getByTweetId(@PathVariable Long tweetId) {
        return ResponseEntity.ok(service.getByTweetId(tweetId));
    }
}