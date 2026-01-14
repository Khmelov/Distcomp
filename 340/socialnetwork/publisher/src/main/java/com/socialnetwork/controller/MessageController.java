package com.socialnetwork.controller;

import com.socialnetwork.dto.request.MessageRequestTo;
import com.socialnetwork.dto.response.MessageResponseTo;
import com.socialnetwork.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping
    public ResponseEntity<List<MessageResponseTo>> getAllMessages() {
        List<MessageResponseTo> messages = messageService.getAll();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseTo> getMessageById(@PathVariable Long id) {
        MessageResponseTo message = messageService.getById(id);
        return ResponseEntity.ok(message);
    }

    @PostMapping
    public ResponseEntity<MessageResponseTo> createMessage(@Valid @RequestBody MessageRequestTo request) {
        // Если country не указан, устанавливаем значение по умолчанию
        if (request.getCountry() == null || request.getCountry().trim().isEmpty()) {
            request.setCountry("US");
        }

        MessageResponseTo createdMessage = messageService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseTo> updateMessage(@PathVariable Long id,
                                                           @Valid @RequestBody MessageRequestTo request) {
        // Если country не указан, устанавливаем значение по умолчанию
        if (request.getCountry() == null || request.getCountry().trim().isEmpty()) {
            request.setCountry("US");
        }

        MessageResponseTo updatedMessage = messageService.update(id, request);
        return ResponseEntity.ok(updatedMessage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<MessageResponseTo>> getMessagesByTweetId(@PathVariable Long tweetId) {
        List<MessageResponseTo> messages = messageService.getByTweetId(tweetId);
        return ResponseEntity.ok(messages);
    }
}