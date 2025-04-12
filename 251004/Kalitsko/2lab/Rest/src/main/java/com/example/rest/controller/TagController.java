package com.example.rest.controller;


import com.example.rest.dto.tag.TagRequestTo;
import com.example.rest.dto.tag.TagResponseTo;

import com.example.rest.dto.tag.TagUpdate;
import com.example.rest.exceptionHandler.CreatorNotFoundException;
import com.example.rest.service.TagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1.0/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<List<TagResponseTo>> findAll() {
        return ResponseEntity.ok(tagService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponseTo> findById(@PathVariable Long id) {
        Optional<TagResponseTo> creator = tagService.findById(id);
        return creator.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TagResponseTo> create(@Valid @RequestBody TagRequestTo creatorRequestTo) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tagService.create(creatorRequestTo));

    }

    @PutMapping()
    public ResponseEntity<TagResponseTo> update(@Valid @RequestBody TagUpdate creatorUpdate) {
        return ResponseEntity.ok(tagService.update(creatorUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tagService.findById(id).orElseThrow(() -> new CreatorNotFoundException(id));
        tagService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
