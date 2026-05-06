package com.example.news.controller;

import com.example.common.dto.MessageRequestTo;
import com.example.common.dto.MessageResponseTo;
import com.example.news.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<MessageResponseTo>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(messageService.findAll(page, size, sortBy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseTo> findById(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MessageResponseTo> create(@Valid @RequestBody MessageRequestTo request) {
        return new ResponseEntity<>(messageService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody MessageRequestTo request) {
        return ResponseEntity.ok(messageService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        messageService.delete(id);
    }
}