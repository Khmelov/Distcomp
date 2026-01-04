package com.blog.controller;

import com.blog.dto.request.TagRequestTo;
import com.blog.dto.response.MessageResponseTo;
import com.blog.dto.response.TagResponseTo;
import com.blog.service.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tags")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping
    public ResponseEntity<List<TagResponseTo>> getAllTag(){
        List<TagResponseTo> tag = tagService.getAll();
        return ResponseEntity.ok(tag);
    }

    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public List<TagResponseTo> getAllTagsList() {
        return tagService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponseTo> getTagById(@PathVariable Long id) {
        TagResponseTo tag = tagService.getById(id);
        return ResponseEntity.ok(tag);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<TagResponseTo> getTagByName(@PathVariable String name) {
        TagResponseTo tag = tagService.findByName(name);
        return ResponseEntity.ok(tag);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TagResponseTo createTag(@Valid @RequestBody TagRequestTo request) {
        return tagService.create(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponseTo> updateTag(@PathVariable Long id,
                                                   @Valid @RequestBody TagRequestTo request) {
        TagResponseTo updatedTag = tagService.update(id, request);
        return ResponseEntity.ok(updatedTag);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTag(@PathVariable Long id) {
        tagService.delete(id);
    }
}