package com.example.demo.controllers;

import com.example.demo.dto.requests.MessageRequestTo;
import com.example.demo.dto.responses.MessageResponseTo;
import com.example.demo.service.MessageClientService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/messages")
@Validated
public class MessageController {
    private final MessageClientService messageService;

    public MessageController(MessageClientService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<MessageResponseTo> create(@Valid @RequestBody MessageRequestTo dto) throws JsonProcessingException {
        MessageResponseTo response = messageService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /*
    @GetMapping
    public ResponseEntity<Map<String, Object>> findAll(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id,asc") String sort,
            @RequestParam(name = "content", required = false) String content,
            @RequestParam(name = "issueId", required = false) Long issueId
    ) {
        Pageable pageable;
        if (page != null && size != null) {
            pageable = PageRequest.of(page, size, parseSort(sort));
        } else {
            pageable = Pageable.unpaged();
        }
        Page<MessageResponseTo> pageResult = messageService.findAll(pageable, content, issueId);

        Map<String, Object> response = new HashMap<>();
        response.put("content", pageResult.getContent());
        response.put("totalElements", pageResult.getTotalElements());
        response.put("totalPages", pageResult.getTotalPages());
        response.put("size", pageResult.getSize());
        response.put("number", pageResult.getNumber());

        return ResponseEntity.ok(response);
    }

     */

    @GetMapping
    public ResponseEntity<List<MessageResponseTo>> findAll(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id,asc") String sort,
            @RequestParam(name = "content", required = false) String content,
            @RequestParam(name = "issueId", required = false) Long issueId
    ) {
        Pageable pageable = (page != null && size != null) ? PageRequest.of(page, size, parseSort(sort)) : Pageable.unpaged();
        Page<MessageResponseTo> pageResult = messageService.findAll(pageable, content, issueId);
        return ResponseEntity.ok(pageResult.getContent());
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseTo> findById(@PathVariable("id") Long id) {
        MessageResponseTo message = messageService.findById(id);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseTo> update(@PathVariable("id") Long id,
                                                    @Valid @RequestBody MessageRequestTo dto) {
        MessageResponseTo updated = messageService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
