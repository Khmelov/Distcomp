package com.labs.web.controller.v1;

import com.labs.service.dto.WriterDto;
import com.labs.service.service.WriterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/writers")
@RequiredArgsConstructor
public class WriterController {
    private final WriterService writerService;

    @PostMapping
    public ResponseEntity<WriterDto> create(@Valid @RequestBody WriterDto writerDto) {
        WriterDto created = writerService.create(writerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<WriterDto>> findAll() {
        List<WriterDto> writers = writerService.findAll();
        return ResponseEntity.ok(writers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WriterDto> findById(@PathVariable Long id) {
        WriterDto writer = writerService.findById(id);
        return ResponseEntity.ok(writer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WriterDto> update(@PathVariable Long id, @Valid @RequestBody WriterDto writerDto) {
        WriterDto updated = writerService.update(id, writerDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        writerService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

