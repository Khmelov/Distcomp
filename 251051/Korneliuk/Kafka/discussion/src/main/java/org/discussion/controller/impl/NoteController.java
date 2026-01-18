package org.discussion.controller.impl;

import com.common.NoteResponseTo;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.discussion.dto.request.NoteRequestToDto;
import org.discussion.service.NoteService;
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
public class NoteController {
    NoteService service;

    @PostMapping("/notes")
    public ResponseEntity<NoteResponseTo> create(@RequestBody NoteRequestToDto request) {
        return ResponseEntity.status(201).body(service.create(request));
    }

    @GetMapping("/notes")
    public ResponseEntity<List<NoteResponseTo>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/notes/{id}")
    public ResponseEntity<NoteResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/notes/issue/{issueId}")
    public ResponseEntity<List<NoteResponseTo>> getByIssueId(@PathVariable Long issueId) {
        return ResponseEntity.ok(service.getByIssueId(issueId));
    }

    @PutMapping("/notes/{id}")
    public ResponseEntity<NoteResponseTo> update(@PathVariable Long id, @RequestBody NoteRequestToDto request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/notes/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}