package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.dto.responses.MessageResponseTo;
import com.example.demo.dto.requests.MessageRequestTo;
import com.example.demo.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1.0/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService service;

    @PostMapping
    public ResponseEntity<MessageResponseTo> create(@Valid @RequestBody MessageRequestTo dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @GetMapping("/{issueId}/{id}")
    public ResponseEntity<MessageResponseTo> findById(@PathVariable Long issueId, @PathVariable Long id) {
        return ResponseEntity.ok(service.findById(issueId, id));
    }

    @GetMapping
    public ResponseEntity<List<MessageResponseTo>> findAll(@RequestParam(required = false) Long issueId) {
        if (issueId != null) {
            return ResponseEntity.ok(service.findAllByIssueId(issueId));
        } else {
            return ResponseEntity.ok(service.findAll());
        }
    }

    @PutMapping("/{issueId}/{id}")
    public ResponseEntity<MessageResponseTo> update(@PathVariable Long issueId,
                                                    @PathVariable Long id,
                                                    @Valid @RequestBody MessageRequestTo dto) {
        dto.setIssueId(issueId);
        return ResponseEntity.ok(service.update(issueId, id, dto));
    }

    @DeleteMapping("/{issueId}/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long issueId, @PathVariable Long id) {
        service.delete(issueId, id);
        return ResponseEntity.noContent().build();
    }
}

