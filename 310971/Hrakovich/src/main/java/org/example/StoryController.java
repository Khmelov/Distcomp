package org.example;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/stories")
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    @GetMapping("/{id}")
    public ResponseEntity<StoryResponseTo> getById(@PathVariable Long id) {
        return storyService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<StoryResponseTo>> getAll() {
        return ResponseEntity.ok(storyService.getAll());
    }

    @PostMapping
    public ResponseEntity<StoryResponseTo> create(
            @RequestBody @Valid StoryRequestTo dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(storyService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoryResponseTo> update(
            @PathVariable Long id,
            @RequestBody @Valid StoryRequestTo dto
    ) {
        return ResponseEntity.ok(storyService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        storyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}