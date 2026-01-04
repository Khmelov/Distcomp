package com.blog.controller.v2;

import com.blog.dto.request.TagRequestTo;
import com.blog.dto.response.TagResponseTo;
import com.blog.service.TagService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/tags")
public class TagControllerV2 {

    @Autowired
    private TagService tagService;

    // Получить все теги (чтение доступно всем аутентифицированным)
    @GetMapping
    public ResponseEntity<List<TagResponseTo>> getAllTags() {
        List<TagResponseTo> tags = tagService.getAll();
        return ResponseEntity.ok(tags);
    }

    // Получить тег по ID (чтение доступно всем аутентифицированным)
    @GetMapping("/{id}")
    public ResponseEntity<TagResponseTo> getTagById(@PathVariable Long id) {
        TagResponseTo tag = tagService.getById(id);
        return ResponseEntity.ok(tag);
    }

    // Получить тег по имени (чтение доступно всем аутентифицированным)
    @GetMapping("/name/{name}")
    public ResponseEntity<TagResponseTo> getTagByName(@PathVariable String name) {
        TagResponseTo tag = tagService.findByName(name);
        return ResponseEntity.ok(tag);
    }

    // Создать тег (только для ADMIN)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TagResponseTo> createTag(@Valid @RequestBody TagRequestTo request) {
        TagResponseTo createdTag = tagService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
    }

    // Обновить тег (только для ADMIN)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TagResponseTo> updateTag(@PathVariable Long id,
                                                   @Valid @RequestBody TagRequestTo request) {
        TagResponseTo updatedTag = tagService.update(id, request);
        return ResponseEntity.ok(updatedTag);
    }

    // Удалить тег (только для ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}