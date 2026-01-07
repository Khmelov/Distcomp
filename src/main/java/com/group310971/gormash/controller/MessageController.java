package com.group310971.gormash.controller;

import com.group310971.gormash.dto.MessageRequestTo;
import com.group310971.gormash.dto.MessageResponseTo;
import com.group310971.gormash.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0/messages")
class MessageController {
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponseTo> createMessage(@Valid @RequestBody MessageRequestTo messageRequestTo) {
        try {
            MessageResponseTo createdMessage = messageService.createMessage(messageRequestTo);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponseTo());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseTo> updateMessage(@PathVariable Long id, @Valid @RequestBody MessageRequestTo messageRequestTo) {
        MessageResponseTo updatedMessage = messageService.updateMessage(id, messageRequestTo);
        return ResponseEntity.ok(updatedMessage);
    }

    @GetMapping
    public ResponseEntity<List<MessageResponseTo>> getAllMessages() {
        List<MessageResponseTo> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseTo> getMessageById(@PathVariable Long id) {
        MessageResponseTo message = messageService.getMessageById(id);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponseTo> deleteMessage(@PathVariable Long id) {
        try {
            MessageResponseTo deleted = messageService.deleteMessage(id);
            return new ResponseEntity<>(deleted, HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
