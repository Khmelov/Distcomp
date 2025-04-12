package com.bsuir.romanmuhtasarov.controllers;

import com.bsuir.romanmuhtasarov.domain.request.MessageRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.MessageResponseTo;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.bsuir.romanmuhtasarov.serivces.MessageService;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<MessageResponseTo> createMessage(@RequestBody MessageRequestTo messageRequestTo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.create(messageRequestTo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseTo> findMessageById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(messageService.findMessageById(id));
    }

    @GetMapping
    public ResponseEntity<List<MessageResponseTo>> findAllMessages() {
        return ResponseEntity.status(HttpStatus.OK).body(messageService.read());
    }

    @PutMapping
    public ResponseEntity<MessageResponseTo> updateMessage(@RequestBody MessageRequestTo messageRequestTo) {
        return ResponseEntity.status(HttpStatus.OK).body(messageService.update(messageRequestTo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteMessageById(@PathVariable Long id) {
        messageService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(id);
    }
}
