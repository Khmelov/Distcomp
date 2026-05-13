package com.adashkevich.jpa.lab.controller;

import com.adashkevich.jpa.lab.dto.request.MessageRequestTo;
import com.adashkevich.jpa.lab.dto.response.MessageResponseTo;
import com.adashkevich.jpa.lab.service.MessageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/messages")
@Validated
public class MessageController {
    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MessageResponseTo> create(@Valid @RequestBody MessageRequestTo body) {
        return ResponseEntity.status(201).body(service.create(body));
    }

    @GetMapping
    public List<MessageResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MessageResponseTo getById(@PathVariable @Positive Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public MessageResponseTo update(@PathVariable @Positive Long id, @Valid @RequestBody MessageRequestTo body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
