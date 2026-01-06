package com.labs.web.controller.v1;

import com.labs.service.dto.MessageDto;
import com.labs.service.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageDto> create(@Valid @RequestBody MessageDto messageDto) {
        MessageDto created = messageService.create(messageDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<MessageDto>> findAll() {
        List<MessageDto> messages = messageService.findAll();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> findById(@PathVariable Long id) {
        MessageDto message = messageService.findById(id);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> update(@PathVariable Long id, @Valid @RequestBody MessageDto messageDto) {
        MessageDto updated = messageService.update(id, messageDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        messageService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

