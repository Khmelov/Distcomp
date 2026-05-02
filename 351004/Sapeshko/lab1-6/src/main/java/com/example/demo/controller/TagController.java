package com.example.demo.controller;

import com.example.demo.entity.Tag;
import com.example.demo.service.TagService;
import com.example.demo.specification.TagSpecification;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tags")
@Profile("docker")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<Tag> create(@Valid @RequestBody Tag tag) {
        return new ResponseEntity<>(tagService.create(tag), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tag> update(@PathVariable Long id, @Valid @RequestBody Tag tag) {
        tag.setId(id);
        return ResponseEntity.ok(tagService.update(tag));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Проверяем, существует ли тег перед удалением
        if (tagService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();   // 404 Not Found
        }
        tagService.delete(id);
        return ResponseEntity.noContent().build();      // 204 No Content
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tag> findById(@PathVariable Long id) {
        return tagService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Плоский список (без пагинации) – для тестов
    @GetMapping
    public ResponseEntity<List<Tag>> findAllList(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Sort sorting = Sort.by(direction, sort[0]);

        Specification<Tag> spec = TagSpecification.nameContains(name);
        List<Tag> tags = tagService.findAll(spec, sorting);
        return ResponseEntity.ok(tags);
    }

    // Пагинация (по требованию задания)
    @GetMapping("/page")
    public ResponseEntity<Page<Tag>> findAllPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(required = false) String name) {

        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Sort sorting = Sort.by(direction, sort[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);

        Specification<Tag> spec = TagSpecification.nameContains(name);
        return ResponseEntity.ok(tagService.findAll(spec, pageable));
    }
}