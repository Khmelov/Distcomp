package com.example.entitiesapp.controllers;

import com.example.entitiesapp.dto.request.WriterRequestTo;
import com.example.entitiesapp.dto.response.WriterResponseTo;
import com.example.entitiesapp.services.WriterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/writers")
@RequiredArgsConstructor
public class WriterController {
    private final WriterService writerService;

    @GetMapping
    public ResponseEntity<List<WriterResponseTo>> getAllWriters() {
        List<WriterResponseTo> writers = writerService.getAll();
        return ResponseEntity.ok(writers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WriterResponseTo> getWriterById(@PathVariable Long id) {
        WriterResponseTo writer = writerService.getById(id);
        return ResponseEntity.ok(writer);
    }

    @PostMapping
    public ResponseEntity<WriterResponseTo> createWriter(@Valid @RequestBody WriterRequestTo writerRequestTo) {
        WriterResponseTo createdWriter = writerService.create(writerRequestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdWriter);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WriterResponseTo> updateWriter(
            @PathVariable Long id,
            @Valid @RequestBody WriterRequestTo writerRequestTo) {
        WriterResponseTo updatedWriter = writerService.update(id, writerRequestTo);
        return ResponseEntity.ok(updatedWriter);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWriter(@PathVariable Long id) {
        writerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}