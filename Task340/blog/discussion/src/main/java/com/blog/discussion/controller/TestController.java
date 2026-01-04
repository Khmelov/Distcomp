package com.blog.discussion.controller;

import com.blog.discussion.dto.request.MessageRequestTo;
import com.blog.discussion.dto.response.MessageResponseTo;
import com.blog.discussion.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0/test")
public class TestController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Discussion service is running");
    }

    @PostMapping("/kafka-test")
    public ResponseEntity<MessageResponseTo> kafkaTest(@RequestBody MessageRequestTo request) {
        // Тестовый метод для проверки сохранения в Cassandra
        MessageResponseTo response = messageService.createMessage(
                request.getCountry() != null ? request.getCountry() : "global",
                request.getTopicId() != null ? request.getTopicId() : 1L,
                request
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/check/{id}")
    public ResponseEntity<MessageResponseTo> checkMessage(@PathVariable Long id) {
        try {
            MessageResponseTo message = messageService.getMessage("global", 1L, id);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            MessageResponseTo error = new MessageResponseTo();
            error.setId(id);
            error.setContent("Not found: " + e.getMessage());
            error.setState("NOT_FOUND");
            return ResponseEntity.ok(error);
        }
    }
}

