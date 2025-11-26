package org.discussion.controller.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.discussion.dto.request.NoticeRequestToDto;
import org.discussion.dto.response.NoticeResponseToDto;
import org.discussion.service.NoticeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NoticeController {
    NoticeService service;

    @PostMapping("/notices")
    public ResponseEntity<NoticeResponseToDto> create(@RequestBody NoticeRequestToDto request) {
        return ResponseEntity.status(201).body(service.create(request));
    }

    @GetMapping("/notices")
    public ResponseEntity<List<NoticeResponseToDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/notices/{id}")
    public ResponseEntity<NoticeResponseToDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/notices/issue/{issueId}")
    public ResponseEntity<List<NoticeResponseToDto>> getByIssueId(@PathVariable Long issueId) {
        return ResponseEntity.ok(service.getByIssueId(issueId));
    }

    @PutMapping("/notices/{id}")
    public ResponseEntity<NoticeResponseToDto> update(@PathVariable Long id, @RequestBody NoticeRequestToDto request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/notices/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}