package org.example.task310rest.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.example.task310rest.dto.MessageRequestTo;
import org.example.task310rest.dto.MessageResponseTo;
import org.example.task310rest.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.0/messages")
public class MessageController {

    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    @GetMapping
    public List<MessageResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MessageResponseTo getById(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<MessageResponseTo> create(@Valid @RequestBody MessageRequestTo request) {
        MessageResponseTo response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public MessageResponseTo update(@PathVariable("id") Long id, @Valid @RequestBody MessageRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}


