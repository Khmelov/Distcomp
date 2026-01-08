package com.task.discussion.controller;

import com.task.discussion.dto.NoticeRequestTo;
import com.task.discussion.dto.NoticeResponseTo;
import com.task.discussion.service.NoticeService;
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
        log.info("POST /notices - Creating new notice");
        NoticeResponseTo response = noticeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<NoticeResponseTo>> getAll() {
        log.info("GET /notices - Retrieving all notices");
        return ResponseEntity.ok(noticeService.getAll());
    }

    // Новый endpoint: GET /notices/{id}
    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponseTo> getByIdOnly(@PathVariable Long id) {
        log.info("GET /notices/{} - Retrieving notice by id only", id);
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
        log.info("GET /notices/{}/{}/{} - Retrieving notice by id", country, tweetId, id);
        NoticeResponseTo response = noticeService.getById(country, tweetId, id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<NoticeResponseTo>> getByTweetId(
            @PathVariable Long tweetId,
            @RequestParam(defaultValue = "BY") String country) {
        log.info("GET /notices/tweet/{} - Retrieving notices by tweetId with country: {}", tweetId, country);
        List<NoticeResponseTo> response = noticeService.getByTweetId(tweetId, country);
        return ResponseEntity.ok(response);
    }

    // Новый endpoint: PUT /notices/{id}
    @PutMapping("/{id}")
    public ResponseEntity<NoticeResponseTo> updateByIdOnly(
            @PathVariable Long id,
            @Valid @RequestBody NoticeRequestTo request) {
        log.info("PUT /notices/{} - Updating notice by id only", id);
        NoticeResponseTo response = noticeService.updateByIdOnly(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{country}/{tweetId}/{id}")
    public ResponseEntity<NoticeResponseTo> update(
            @PathVariable String country,
            @PathVariable Long tweetId,
            @PathVariable Long id,
            @Valid @RequestBody NoticeRequestTo request) {
        log.info("PUT /notices/{}/{}/{} - Updating notice", country, tweetId, id);
        NoticeResponseTo response = noticeService.update(country, tweetId, id, request);
        return ResponseEntity.ok(response);
    }

    // Новый endpoint: DELETE /notices/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteByIdOnly(@PathVariable Long id) {
        log.info("DELETE /notices/{} - Deleting notice by id only", id);
        noticeService.deleteByIdOnly(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{country}/{tweetId}/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String country,
            @PathVariable Long tweetId,
            @PathVariable Long id) {
        log.info("DELETE /notices/{}/{}/{} - Deleting notice", country, tweetId, id);
        noticeService.delete(country, tweetId, id);
        return ResponseEntity.noContent().build();
    }
}
