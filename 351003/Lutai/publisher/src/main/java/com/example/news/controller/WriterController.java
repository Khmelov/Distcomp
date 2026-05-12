package com.example.news.controller;

import com.example.common.dto.WriterRequestTo;
import com.example.common.dto.WriterResponseTo;
import com.example.news.service.WriterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/{version}/writers")
@RequiredArgsConstructor
public class WriterController {

    private final WriterService writerService;

    @GetMapping
    public ResponseEntity<List<WriterResponseTo>> findAll(
            @PathVariable("version") String version,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(writerService.findAll(page, size, sortBy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WriterResponseTo> findById(@PathVariable("version") String version, @PathVariable("id") Long id) {
        return ResponseEntity.ok(writerService.findById(id));
    }

    @PostMapping
    public ResponseEntity<WriterResponseTo> create(
            @PathVariable("version") String version,
            @Valid @RequestBody WriterRequestTo writerRequestTo) {

        if ("v2.0".equals(version)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(writerService.create(writerRequestTo), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WriterResponseTo> update(
            @PathVariable("version") String version,
            @PathVariable("id") Long id,
            @Valid @RequestBody WriterRequestTo request) {
        return ResponseEntity.ok(writerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("version") String version, @PathVariable("id") Long id) {
        writerService.delete(id);
    }
}