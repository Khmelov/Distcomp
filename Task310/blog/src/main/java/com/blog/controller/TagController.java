package com.blog.controller;

import com.blog.dto.TagRequestTo;
import com.blog.dto.TagResponseTo;
import com.blog.service.TagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<List<TagResponseTo>> getAllTags() {
        List<TagResponseTo> tags = tagService.findAll();
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponseTo> getTagById(@PathVariable Long id) {
        TagResponseTo tag = tagService.findById(id);
        return ResponseEntity.ok(tag);
    }

    @PostMapping
    public ResponseEntity<TagResponseTo> createTag(@Valid @RequestBody TagRequestTo request) {
        TagResponseTo createdTag = tagService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponseTo> updateTag(@PathVariable Long id, @Valid @RequestBody TagRequestTo request) {
        TagResponseTo updatedTag = tagService.update(id, request);
        return ResponseEntity.ok(updatedTag);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}