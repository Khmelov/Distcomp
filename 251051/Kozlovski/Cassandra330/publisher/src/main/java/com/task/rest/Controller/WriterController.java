package com.task.rest.Controller;

import com.task.rest.dto.WriterRequestTo;
import com.task.rest.dto.WriterResponseTo;
import com.task.rest.service.WriterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/writers")
public class WriterController {
    private final WriterService writerService;

    public WriterController(WriterService writerService) {
        this.writerService = writerService;
    }

    @PostMapping
    public ResponseEntity<WriterResponseTo> createWriter(@Valid @RequestBody WriterRequestTo requestTo) {
        WriterResponseTo response = writerService.createWriter(requestTo);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WriterResponseTo> getWriterById(@PathVariable Long id) {
        WriterResponseTo response = writerService.getWriterById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<WriterResponseTo>> getAllWriters() {
        List<WriterResponseTo> writers = writerService.getAllWriters();
        return ResponseEntity.ok(writers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WriterResponseTo> updateWriter(@PathVariable Long id, @Valid @RequestBody WriterRequestTo requestTo) {
        WriterResponseTo response = writerService.updateWriter(id, requestTo);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWriter(@PathVariable Long id) {
        writerService.deleteWriter(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-tweet/{tweetId}")
    public ResponseEntity<WriterResponseTo> getWriterByTweetId(@PathVariable Long tweetId) {
        WriterResponseTo response = writerService.getWriterByTweetId(tweetId);
        return ResponseEntity.ok(response);
    }
}
