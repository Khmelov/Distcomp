package com.example.news.controller;

import com.example.common.dto.MessageRequestTo;
import com.example.common.dto.MessageResponseTo;
import com.example.news.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/{version}/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<MessageResponseTo>> findAll(
            @PathVariable String version,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(messageService.findAll(page, size, sortBy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseTo> findById(@PathVariable String version, @PathVariable Long id) {
        return ResponseEntity.ok(messageService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MessageResponseTo> create(@PathVariable String version, @Valid @RequestBody MessageRequestTo request) {
        return new ResponseEntity<>(messageService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("#version == 'v1.0' or hasRole('ADMIN') or @messageService.isOwner(#id, authentication.name)")
    public ResponseEntity<MessageResponseTo> update(
            @PathVariable("version") String version, // Явно указываем имя
            @PathVariable("id") Long id,
            @Valid @RequestBody MessageRequestTo request) {
        return ResponseEntity.ok(messageService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("#version == 'v1.0' or hasRole('ADMIN') or @messageService.isOwner(#id, authentication.name)")
    public void delete(
            @PathVariable("version") String version,
            @PathVariable("id") Long id) {
        messageService.delete(id);
    }
}