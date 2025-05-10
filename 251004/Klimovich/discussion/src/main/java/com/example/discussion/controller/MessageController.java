package com.example.discussion.controller;

import com.example.discussion.dto.MessageRequestTo;
import com.example.discussion.dto.MessageResponseTo;
import com.example.discussion.service.MessageService;
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

    @PostMapping
    public ResponseEntity<MessageResponseTo> createMessage(@RequestBody MessageRequestTo request) {
        MessageResponseTo response = messageService.createMessage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MessageResponseTo>> getAllMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseTo> getMessageById(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.getMessageById(id));
    }

    @PutMapping
    public ResponseEntity<MessageResponseTo> updateMessage(@RequestBody MessageRequestTo request) {
        return ResponseEntity.ok(messageService.updateMessage(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}
