package com.blog.controller;

import com.blog.dto.WriterRequestTo;
import com.blog.dto.WriterResponseTo;
import com.blog.service.WriterService;
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

    @GetMapping
    public ResponseEntity<List<WriterResponseTo>> getAllWriters() {
        List<WriterResponseTo> writers = writerService.findAll();
        return ResponseEntity.ok(writers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WriterResponseTo> getWriterById(@PathVariable Long id) {
        WriterResponseTo writer = writerService.findById(id);
        return ResponseEntity.ok(writer);
    }

    @PostMapping
    public ResponseEntity<WriterResponseTo> createWriter(@Valid @RequestBody WriterRequestTo request) {
        WriterResponseTo createdWriter = writerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWriter);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WriterResponseTo> updateWriter(@PathVariable Long id, @Valid @RequestBody WriterRequestTo request) {
        WriterResponseTo updatedWriter = writerService.update(id, request);
        return ResponseEntity.ok(updatedWriter);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWriter(@PathVariable Long id) {
        writerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}