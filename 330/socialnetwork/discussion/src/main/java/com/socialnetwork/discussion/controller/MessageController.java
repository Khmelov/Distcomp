package com.socialnetwork.discussion.controller;

import com.socialnetwork.discussion.dto.request.MessageRequestDto;
import com.socialnetwork.discussion.dto.response.MessageResponseDto;
import com.socialnetwork.discussion.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<List<MessageResponseDto>> getAllMessages() {
        List<MessageResponseDto> messages = messageService.getAll();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseDto> getMessageById(@PathVariable Long id) {
        try {
            MessageResponseDto message = messageService.getById(id);
            return ResponseEntity.ok(message);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<MessageResponseDto> createMessage(@Valid @RequestBody MessageRequestDto request) {
        // Если country не указан, устанавливаем значение по умолчанию
        if (request.getCountry() == null || request.getCountry().trim().isEmpty()) {
            request.setCountry("US");
        }

        MessageResponseDto createdMessage = messageService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseDto> updateMessage(@PathVariable Long id,
                                                            @Valid @RequestBody MessageRequestDto request) {
        try {
            // Если country не указан, устанавливаем значение по умолчанию
            if (request.getCountry() == null || request.getCountry().trim().isEmpty()) {
                request.setCountry("US");
            }

            MessageResponseDto updatedMessage = messageService.update(id, request);
            return ResponseEntity.ok(updatedMessage);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        try {
            messageService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<MessageResponseDto>> getMessagesByTweetId(@PathVariable Long tweetId) {
        List<MessageResponseDto> messages = messageService.getByTweetId(tweetId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/country/{country}/tweet/{tweetId}")
    public ResponseEntity<List<MessageResponseDto>> getMessagesByCountryAndTweetId(
            @PathVariable String country,
            @PathVariable Long tweetId) {
        List<MessageResponseDto> messages = messageService.getByCountryAndTweetId(country, tweetId);
        return ResponseEntity.ok(messages);
    }
}