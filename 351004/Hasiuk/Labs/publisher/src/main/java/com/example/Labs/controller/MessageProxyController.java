package com.example.Labs.controller;

import com.example.Labs.client.MessageRestClient;
import com.example.Labs.client.StoryClient;
import com.example.Labs.dto.request.MessageRequestTo;
import com.example.Labs.dto.response.ErrorResponse;
import com.example.Labs.dto.response.MessageResponseTo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/messages")
@RequiredArgsConstructor
public class MessageProxyController {

    private final MessageRestClient messageRestClient;
    private final StoryClient storyClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageResponseTo create(@Valid @RequestBody MessageRequestTo request) {
        // Проверка: storyId не должен быть null
        if (request.getStoryId() == null || request.getStoryId() <= 0) {
            throw new IllegalArgumentException("Invalid storyId: " + request.getStoryId());
        }

        // Проверка: существует ли story с таким ID
        boolean storyExists = storyClient.existsById(request.getStoryId());
        if (!storyExists) {
            throw new IllegalArgumentException("Story not found with id: " + request.getStoryId());
        }

        return messageRestClient.create(request);
    }

    @GetMapping
    public List<MessageResponseTo> getAll() {
        return messageRestClient.getAll();
    }

    @GetMapping("/{id}")
    public MessageResponseTo getById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid id: " + id);
        }
        return messageRestClient.getById(id);
    }

    @GetMapping("/by-story/{storyId}")
    public List<MessageResponseTo> getByStoryId(@PathVariable Long storyId) {
        if (storyId == null || storyId <= 0) {
            throw new IllegalArgumentException("Invalid storyId: " + storyId);
        }
        return messageRestClient.getByStoryId(storyId);
    }

    @PutMapping("/{id}")
    public MessageResponseTo update(@PathVariable Long id, @Valid @RequestBody MessageRequestTo request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid id: " + id);
        }
        return messageRestClient.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid id: " + id);
        }
        messageRestClient.delete(id);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("40001", ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("40401", ex.getMessage()));
    }
}