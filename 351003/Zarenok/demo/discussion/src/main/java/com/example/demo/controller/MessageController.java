package com.example.demo.controller;
import com.example.demo.dto.responses.MessageResponseTo;
import com.example.demo.dto.requests.MessageRequestTo;
import com.example.demo.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService service;

    @PostMapping
    public ResponseEntity<MessageResponseTo> create(@Valid @RequestBody MessageRequestTo dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    // GET /api/v1.0/messages/{issueId}/{id}
    @GetMapping("/{issueId}/{id}")
    public ResponseEntity<MessageResponseTo> findById(@PathVariable("issueId") Long issueId,
                                                      @PathVariable("id") Long id) {
        return ResponseEntity.ok(service.findById(issueId, id));
    }

    // GET /api/v1.0/messages?page=0&size=10&sort=id,asc&issueId=123&content=text
    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id,asc") String sort,
            @RequestParam(name = "issueId", required = false) Long issueId,
            @RequestParam(name = "content", required = false) String content) {

        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size, parseSort(sort));
            return ResponseEntity.ok(service.findAll(pageable, issueId, content));
        } else {
            List<MessageResponseTo> list = service.findAll(content, issueId);
            return ResponseEntity.ok(list);
        }
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        String field = parts[0];
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1])
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, field);
    }

    // GET /api/v1.0/messages/{id}  (поиск по одному id)
    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseTo> findByIdOnly(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.findByIdOnly(id));
    }

    // PUT /api/v1.0/messages/{issueId}/{id}
    @PutMapping("/{issueId}/{id}")
    public ResponseEntity<MessageResponseTo> update(@PathVariable("issueId") Long issueId,
                                                    @PathVariable("id") Long id,
                                                    @Valid @RequestBody MessageRequestTo dto) {
        dto.setIssueId(issueId);
        return ResponseEntity.ok(service.update(issueId, id, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseTo> updateByIdOnly(
            @PathVariable("id") Long id,
            @Valid @RequestBody MessageRequestTo dto) {
        // Обновляем сообщение, найденное по id (через вторичный индекс)
        return ResponseEntity.ok(service.updateByIdOnly(id, dto));
    }

    // DELETE /api/v1.0/messages/{issueId}/{id}
    @DeleteMapping("/{issueId}/{id}")
    public ResponseEntity<Void> delete(@PathVariable("issueId") Long issueId,
                                       @PathVariable("id") Long id) {
        service.delete(issueId, id);
        return ResponseEntity.noContent().build();
    }
}

