package com.example.app.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.app.dto.TagRequestDTO;
import com.example.app.dto.TagResponseDTO;
import com.example.app.service.TagService;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tags")
public class TagController {
    private final TagService service;

    public TagController(TagService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<TagResponseDTO>> getTags() {
        return ResponseEntity.ok(service.getAllTags());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponseDTO> getTag(@PathVariable Long id) {
        return ResponseEntity.ok(service.getTagById(id));
    }

    @PostMapping
    public ResponseEntity<TagResponseDTO> createTag(@Valid @RequestBody TagRequestDTO request) {
        TagResponseDTO created = service.createTag(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping
    public ResponseEntity<TagResponseDTO> updateTag(@Valid @RequestBody TagRequestDTO request) {
        return ResponseEntity.ok(service.updateTag(request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable Long id) {
        service.deleteTag(id);
    }
}