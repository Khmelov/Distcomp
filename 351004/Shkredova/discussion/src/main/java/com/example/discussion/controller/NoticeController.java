package com.example.discussion.controller;

import com.example.discussion.dto.NoticeRequestTo;
import com.example.discussion.dto.NoticeResponseTo;
import com.example.discussion.service.NoticeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/notices")
public class NoticeController {
    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping
    public ResponseEntity<NoticeResponseTo> create(@Valid @RequestBody NoticeRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noticeService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<NoticeResponseTo>> findAll() {
        return ResponseEntity.ok(noticeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponseTo> findById(@PathVariable Long id) {
        return noticeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/news/{newsId}")
    public ResponseEntity<List<NoticeResponseTo>> findByNewsId(@PathVariable Long newsId) {
        return ResponseEntity.ok(noticeService.findAllByNewsId(newsId));
    }

    @GetMapping("/news/{newsId}/id/{id}")
    public ResponseEntity<NoticeResponseTo> findByNewsIdAndId(@PathVariable Long newsId, @PathVariable Long id) {
        return noticeService.findByNewsIdAndId(newsId, id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping
    public ResponseEntity<NoticeResponseTo> update(@Valid @RequestBody NoticeRequestTo request) {
        return ResponseEntity.ok(noticeService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        var notice = noticeService.findById(id);
        if (notice.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        noticeService.delete(notice.get().getNewsId(), id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/news/{newsId}/id/{id}")
    public ResponseEntity<Void> deleteWithNewsId(@PathVariable Long newsId, @PathVariable Long id) {
        noticeService.delete(newsId, id);
        return ResponseEntity.noContent().build();
    }
}