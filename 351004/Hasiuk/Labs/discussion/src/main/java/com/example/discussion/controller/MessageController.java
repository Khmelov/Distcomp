package com.example.discussion.controller;

import com.example.discussion.dto.request.MessageRequestTo;
import com.example.discussion.dto.response.MessageResponseTo;
import com.example.discussion.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseTo create(@Valid @RequestBody MessageRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<MessageResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MessageResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/by-story/{storyId}")
    public List<MessageResponseTo> getByStoryId(@PathVariable Long storyId) {
        return service.getByStoryId(storyId);
    }

    @PutMapping("/{id}")
    public MessageResponseTo update(@PathVariable Long id, @Valid @RequestBody MessageRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}