package com.task310.blogplatform.controller;

import com.task310.blogplatform.dto.LabelRequestTo;
import com.task310.blogplatform.dto.LabelResponseTo;
import com.task310.blogplatform.service.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
public class LabelController {
    private final LabelService labelService;

    @Autowired
    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @PostMapping("/labels")
    public ResponseEntity<LabelResponseTo> createLabel(@RequestBody LabelRequestTo labelRequestTo) {
        LabelResponseTo created = labelService.create(labelRequestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/labels")
    public ResponseEntity<List<LabelResponseTo>> getAllLabels() {
        List<LabelResponseTo> labels = labelService.findAll();
        return ResponseEntity.ok(labels);
    }

    @GetMapping("/labels/{id}")
    public ResponseEntity<LabelResponseTo> getLabelById(@PathVariable Long id) {
        LabelResponseTo label = labelService.findById(id);
        return ResponseEntity.ok(label);
    }

    @PutMapping("/labels/{id}")
    public ResponseEntity<LabelResponseTo> updateLabel(@PathVariable Long id, @RequestBody LabelRequestTo labelRequestTo) {
        LabelResponseTo updated = labelService.update(id, labelRequestTo);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/labels/{id}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long id) {
        labelService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

