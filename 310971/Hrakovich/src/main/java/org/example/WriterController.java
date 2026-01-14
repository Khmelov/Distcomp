package org.example;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/writers")
@RequiredArgsConstructor
public class WriterController {

    private final WriterService writerService;

    @GetMapping("/{id}")
    public ResponseEntity<WriterResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(writerService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<WriterResponseTo>> getAll() {
        return ResponseEntity.ok(writerService.getAll());
    }

    @PostMapping
    public ResponseEntity<WriterResponseTo> create(
            @RequestBody @Valid WriterRequestTo dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(writerService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WriterResponseTo> update(
            @PathVariable Long id,
            @RequestBody @Valid WriterRequestTo dto
    ) {
        return ResponseEntity.ok(writerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        writerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}