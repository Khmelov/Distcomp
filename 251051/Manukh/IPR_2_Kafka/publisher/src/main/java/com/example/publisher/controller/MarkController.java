// MarkController.java
package com.example.publisher.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.publisher.dto.request.MarkRequestTo;
import com.example.publisher.dto.response.MarkResponseTo;
import com.example.publisher.service.MarkService;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
public class MarkController {

    @Autowired
    private MarkService markService;

    @GetMapping("/marks")
    public ResponseEntity<List<MarkResponseTo>> getAllMarks() {
        return ResponseEntity.ok(markService.getAllMarks());
    }

    @GetMapping("/marks/{id}")
    public ResponseEntity<MarkResponseTo> getMarkById(@PathVariable Long id) {
        return ResponseEntity.ok(markService.getMarkById(id));
    }

    @PostMapping("/marks")
    public ResponseEntity<MarkResponseTo> createMark(@Valid @RequestBody MarkRequestTo request) {
        MarkResponseTo response = markService.createMark(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/marks/{id}")
    public ResponseEntity<MarkResponseTo> updateMark(
            @PathVariable Long id,
            @Valid @RequestBody MarkRequestTo request) {
        MarkResponseTo response = markService.updateMark(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/marks/{id}")
    public ResponseEntity<Void> deleteMark(@PathVariable Long id) {
        markService.deleteMark(id);
        return ResponseEntity.noContent().build();
    }

    // Additional endpoints for relationships
    @GetMapping("/stories/{storyId}/marks")
    public ResponseEntity<List<MarkResponseTo>> getMarksByStoryId(@PathVariable Long storyId) {
        return ResponseEntity.ok(markService.getMarksByStoryId(storyId));
    }
}