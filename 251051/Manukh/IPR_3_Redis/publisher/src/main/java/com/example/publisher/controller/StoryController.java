// StoryController.java
package com.example.publisher.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.publisher.dto.request.StoryRequestTo;
import com.example.publisher.dto.response.StoryResponseTo;
import com.example.publisher.service.StoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
public class StoryController {

    @Autowired
    private StoryService storyService;

    @GetMapping("/stories")
    public ResponseEntity<List<StoryResponseTo>> getAllStories() {
        return ResponseEntity.ok(storyService.getAllStories());
    }

    @GetMapping("/stories/{id}")
    public ResponseEntity<StoryResponseTo> getStoryById(@PathVariable Long id) {
        return ResponseEntity.ok(storyService.getStoryById(id));
    }

    @PostMapping("/stories")
    public ResponseEntity<StoryResponseTo> createStory(@Valid @RequestBody StoryRequestTo request) {
        StoryResponseTo response = storyService.createStory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/stories/{id}")
    public ResponseEntity<StoryResponseTo> updateStory(
            @PathVariable Long id,
            @Valid @RequestBody StoryRequestTo request) {
        StoryResponseTo response = storyService.updateStory(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/stories/{id}")
    public ResponseEntity<Void> deleteStory(@PathVariable Long id) {
        storyService.deleteStory(id);
        return ResponseEntity.noContent().build();
    }

    // Additional endpoints for relationships
    @GetMapping("/editors/{editorId}/stories")
    public ResponseEntity<List<StoryResponseTo>> getStoriesByEditorId(@PathVariable Long editorId) {
        return ResponseEntity.ok(storyService.getStoriesByEditorId(editorId));
    }

    @PostMapping("/stories/{storyId}/marks/{markId}")
    public ResponseEntity<Void> addMarkToStory(
            @PathVariable Long storyId,
            @PathVariable Long markId) {
        storyService.addMarkToStory(storyId, markId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/stories/{storyId}/marks/{markId}")
    public ResponseEntity<Void> removeMarkFromStory(
            @PathVariable Long storyId,
            @PathVariable Long markId) {
        storyService.removeMarkFromStory(storyId, markId);
        return ResponseEntity.ok().build();
    }
}