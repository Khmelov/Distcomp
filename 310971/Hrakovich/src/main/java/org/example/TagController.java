package org.example;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping("/{id}")
    public ResponseEntity<TagResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<TagResponseTo>> getAll() {
        return ResponseEntity.ok(tagService.getAll());
    }

    @PostMapping
    public ResponseEntity<TagResponseTo> create(
            @RequestBody @Valid TagRequestTo dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(tagService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponseTo> update(
            @PathVariable Long id,
            @RequestBody @Valid TagRequestTo dto
    ) {
        return ResponseEntity.ok(tagService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }
}