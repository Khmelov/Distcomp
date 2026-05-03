package com.example.discussion.controller;

import com.example.common.dto.MessageRequestTo;
import com.example.common.dto.MessageResponseTo;
import com.example.discussion.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseTo create(@RequestBody MessageRequestTo request) {
        return messageService.create(request);
    }

    @GetMapping("/article/{articleId}")
    public List<MessageResponseTo> getByArticleId(@PathVariable Long articleId) {
        return messageService.findAllByArticleId(articleId);
    }
    @GetMapping
    public List<MessageResponseTo> getAll() {
        return messageService.findAll();
    }
    @GetMapping("/{id}")
    public MessageResponseTo getById(@PathVariable Long id) {
        return messageService.findById(id);
    }

    @PutMapping("/{id}")
    public MessageResponseTo update(@PathVariable Long id, @RequestBody MessageRequestTo request) {
        return messageService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        messageService.delete(id);
    }
}