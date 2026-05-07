package com.example.demo.controllers;

import com.example.demo.dto.requests.IssueRequestTo;
import com.example.demo.dto.requests.MessageRequestTo;
import com.example.demo.dto.responses.IssueResponseTo;
import com.example.demo.dto.responses.MessageResponseTo;
import com.example.demo.service.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v1.0/messages")
@Validated
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<MessageResponseTo> create(@RequestBody MessageRequestTo dto){
        MessageResponseTo response = messageService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MessageResponseTo>> findAll(){
        List<MessageResponseTo> list = messageService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseTo> findById(@PathVariable Long id){
        MessageResponseTo message = messageService.findById(id);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageResponseTo> update(@PathVariable Long id,
                                                  @RequestBody MessageRequestTo dto){
        MessageResponseTo updated = messageService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
