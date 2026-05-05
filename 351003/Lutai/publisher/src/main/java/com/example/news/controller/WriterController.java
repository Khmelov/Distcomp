package com.example.news.controller;

import com.example.common.dto.WriterRequestTo;
import com.example.common.dto.WriterResponseTo;
import com.example.news.service.WriterService;
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
    public ResponseEntity<List<WriterResponseTo>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(writerService.findAll(page, size, sortBy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WriterResponseTo> findById(@PathVariable Long id) {
        return ResponseEntity.ok(writerService.findById(id));
    }

    @PostMapping
    public ResponseEntity<WriterResponseTo> create(@Valid @RequestBody WriterRequestTo request) {
        return new ResponseEntity<>(writerService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WriterResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody WriterRequestTo request) {
        return ResponseEntity.ok(writerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        writerService.delete(id);
    }
}