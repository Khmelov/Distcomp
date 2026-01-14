package com.socialnetwork.discussion.controller;

import com.socialnetwork.discussion.dto.request.MessageRequestDto;
import com.socialnetwork.discussion.dto.response.MessageResponseDto;
import com.socialnetwork.discussion.model.Message;
import com.socialnetwork.discussion.repository.MessageRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1.0/messages")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping
    public ResponseEntity<List<MessageResponseDto>> getAllMessages() {
        List<Message> messages = messageRepository.findAll();
        List<MessageResponseDto> response = messages.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getMessageById(@PathVariable Long id) {
        Message message = messageRepository.findById(id)
                .orElse(null);

        if (message == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", java.time.LocalDateTime.now());
            errorResponse.put("status", HttpStatus.NOT_FOUND.value());
            errorResponse.put("error", "Not Found");
            errorResponse.put("message", "Message not found with id: " + id);
            errorResponse.put("path", "/api/v1.0/messages/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        return ResponseEntity.ok(toResponseDto(message));
    }

    @PostMapping
    public ResponseEntity<MessageResponseDto> createMessage(@Valid @RequestBody MessageRequestDto request) {
        String state = moderateContent(request.getContent());

        Message message = new Message(
                request.getCountry() != null ? request.getCountry() : "US",
                request.getTweetId(),
                Math.abs(UUID.randomUUID().getMostSignificantBits()),
                request.getContent(),
                state
        );

        Message saved = messageRepository.save(message);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMessage(@PathVariable Long id,
                                           @Valid @RequestBody MessageRequestDto request) {
        Message message = messageRepository.findById(id)
                .orElse(null);

        if (message == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", java.time.LocalDateTime.now());
            errorResponse.put("status", HttpStatus.NOT_FOUND.value());
            errorResponse.put("error", "Not Found");
            errorResponse.put("message", "Message not found with id: " + id);
            errorResponse.put("path", "/api/v1.0/messages/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        String state = moderateContent(request.getContent());
        message.setContent(request.getContent());
        message.setCountry(request.getCountry() != null ? request.getCountry() : "US");
        message.setTweetId(request.getTweetId());
        message.setState(state);

        Message updated = messageRepository.save(message);
        return ResponseEntity.ok(toResponseDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id) {
        Message message = messageRepository.findById(id)
                .orElse(null);

        if (message == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("timestamp", java.time.LocalDateTime.now());
            errorResponse.put("status", HttpStatus.NOT_FOUND.value());
            errorResponse.put("error", "Not Found");
            errorResponse.put("message", "Message not found with id: " + id);
            errorResponse.put("path", "/api/v1.0/messages/" + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        messageRepository.delete(message);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<MessageResponseDto>> getMessagesByTweetId(@PathVariable Long tweetId) {
        List<Message> messages = messageRepository.findByTweetId(tweetId);
        List<MessageResponseDto> response = messages.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/country/{country}/tweet/{tweetId}")
    public ResponseEntity<List<MessageResponseDto>> getMessagesByCountryAndTweetId(
            @PathVariable String country,
            @PathVariable Long tweetId) {
        List<Message> messages = messageRepository.findByCountryAndTweetId(country, tweetId);
        List<MessageResponseDto> response = messages.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private MessageResponseDto toResponseDto(Message message) {
        return new MessageResponseDto(
                message.getCountry(),
                message.getTweetId(),
                message.getId(),
                message.getContent()
        );
    }

    private String moderateContent(String content) {
        String[] stopWords = {"spam", "scam", "fraud", "illegal"};
        for (String word : stopWords) {
            if (content.toLowerCase().contains(word)) {
                return "DECLINE";
            }
        }
        return "APPROVE";
    }
}