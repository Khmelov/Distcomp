package com.labs.web.controller.v1;

import com.labs.service.dto.LabelDto;
import com.labs.service.service.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/labels")
@RequiredArgsConstructor
public class LabelController {
    private final LabelService labelService;

    @PostMapping
    public ResponseEntity<LabelDto> create(@Valid @RequestBody LabelDto labelDto) {
        LabelDto created = labelService.create(labelDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<LabelDto>> findAll() {
        List<LabelDto> labels = labelService.findAll();
        return ResponseEntity.ok(labels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelDto> findById(@PathVariable Long id) {
        LabelDto label = labelService.findById(id);
        return ResponseEntity.ok(label);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabelDto> update(@PathVariable Long id, @Valid @RequestBody LabelDto labelDto) {
        LabelDto updated = labelService.update(id, labelDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        labelService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

