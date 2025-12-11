package com.task.rest.Controller;

import com.task.rest.dto.MarkRequestTo;
import com.task.rest.dto.MarkResponseTo;
import com.task.rest.service.MarkService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/marks")
public class MarkController {
    private final MarkService markService;

    public MarkController(MarkService markService) {
        this.markService = markService;
    }

    @PostMapping
    public ResponseEntity<MarkResponseTo> createMark(@Valid @RequestBody MarkRequestTo requestTo) {
        MarkResponseTo response = markService.createMark(requestTo);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkResponseTo> getMarkById(@PathVariable Long id) {
        MarkResponseTo response = markService.getMarkById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MarkResponseTo>> getAllMarks() {
        List<MarkResponseTo> marks = markService.getAllMarks();
        return ResponseEntity.ok(marks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarkResponseTo> updateMark(@PathVariable Long id, @Valid @RequestBody MarkRequestTo requestTo) {
        MarkResponseTo response = markService.updateMark(id, requestTo);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMark(@PathVariable Long id) {
        markService.deleteMark(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-tweet/{tweetId}")
    public ResponseEntity<List<MarkResponseTo>> getMarksByTweetId(@PathVariable Long tweetId) {
        List<MarkResponseTo> marks = markService.getMarksByTweetId(tweetId);
        return ResponseEntity.ok(marks);
    }
}
