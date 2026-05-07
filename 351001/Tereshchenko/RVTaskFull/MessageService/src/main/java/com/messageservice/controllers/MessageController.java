package com.messageservice.controllers;

import com.messageservice.dtos.MessageRequestTo;
import com.messageservice.dtos.MessageResponseTo;
import com.messageservice.services.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/messages")
    public ResponseEntity<MessageResponseTo> createMessage(@Valid @RequestBody MessageRequestTo request) {
        return new ResponseEntity<>(messageService.createMessage(request), HttpStatus.CREATED);
    }

    @GetMapping("/messages")
    public ResponseEntity<List<MessageResponseTo>> getAllMessages() {
        return ResponseEntity.ok(messageService.findAllMessages());
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<MessageResponseTo> getMessageById(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.findMessageById(id));
    }

    @PutMapping("/messages/{id}")
    public ResponseEntity<MessageResponseTo> updateMessageById(@Valid @RequestBody MessageRequestTo request, @PathVariable Long id) {
        return ResponseEntity.ok(messageService.updateMessageById(request, id));
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<Void> deleteMessageById(@PathVariable Long id) {
        messageService.deleteMessageById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/messages/tweets/{tweetId}")
    public ResponseEntity<Void> deleteMessageByTweetId(@PathVariable Long tweetId) {
        messageService.deleteMessageByTweetId(tweetId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/messages/{tweetId}/tweet")
    public ResponseEntity<List<MessageResponseTo>> getAllMessagesByTweetId(@PathVariable Long tweetId) {
        return new ResponseEntity<>(messageService.findMessagesByTweetId(tweetId), HttpStatus.OK);
    }
}
