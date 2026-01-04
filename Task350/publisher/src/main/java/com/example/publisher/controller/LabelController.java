package com.example.publisher.controller;

import com.example.publisher.dto.LabelRequestTo;
import com.example.publisher.dto.LabelResponseTo;
import com.example.publisher.service.LabelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/labels")
public class LabelController {
    private final LabelService service;

    public LabelController(LabelService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<LabelResponseTo>> getLabels() {
        return ResponseEntity.ok(service.getAllLabels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelResponseTo> getLabel(@PathVariable Long id) {
        return ResponseEntity.ok(service.getLabelById(id));
    }

    @PostMapping
    public ResponseEntity<LabelResponseTo> createLabel(@Valid @RequestBody LabelRequestTo request) {
        LabelResponseTo created = service.createLabel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping
    public ResponseEntity<LabelResponseTo> updateLabel(@Valid @RequestBody LabelRequestTo request) {
        return ResponseEntity.ok(service.updateLabel(request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLabel(@PathVariable Long id) {
        service.deleteLabel(id);
    }
}