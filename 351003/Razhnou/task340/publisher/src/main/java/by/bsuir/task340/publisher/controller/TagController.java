package by.bsuir.task340.publisher.controller;

import by.bsuir.task340.publisher.dto.request.TagRequestTo;
import by.bsuir.task340.publisher.dto.response.TagResponseTo;
import by.bsuir.task340.publisher.service.TagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tags")
public class TagController {
    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<TagResponseTo> create(@Valid @RequestBody TagRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.create(request));
    }

    @GetMapping
    public List<TagResponseTo> findAll() {
        return tagService.findAll();
    }

    @GetMapping("/{id}")
    public TagResponseTo findById(@PathVariable Long id) {
        return tagService.findById(id);
    }

    @PutMapping
    public TagResponseTo update(@Valid @RequestBody TagRequestTo request) {
        return tagService.update(request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
