package com.example.publisher.controller;

import com.example.publisher.dto.StoryRequestTo;
import com.example.publisher.dto.StoryResponseTo;
import com.example.publisher.service.StoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/stories")
public class StoryController {
    private final StoryService service;

    public StoryController(StoryService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<StoryResponseTo>> getStories() {
        return ResponseEntity.ok(service.getAllStories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryResponseTo> getStory(@PathVariable Long id) {
        return ResponseEntity.ok(service.getStoryById(id));
    }

    @PostMapping
    public ResponseEntity<StoryResponseTo> createStory(@Valid @RequestBody StoryRequestTo request) {
        StoryResponseTo created = service.createStory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping
    public ResponseEntity<StoryResponseTo> updateStory(@Valid @RequestBody StoryRequestTo request) {
        return ResponseEntity.ok(service.updateStory(request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStory(@PathVariable Long id) {
        service.deleteStory(id);
    }
}