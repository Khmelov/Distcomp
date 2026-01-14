package com.socialnetwork.controller;

import com.socialnetwork.dto.request.LabelRequestTo;
import com.socialnetwork.dto.response.LabelResponseTo;
import com.socialnetwork.service.LabelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/labels")
public class LabelController {

    @Autowired
    private LabelService labelService;

    @GetMapping
    public ResponseEntity<List<LabelResponseTo>> getAllLabels() {
        List<LabelResponseTo> labels = labelService.getAll();
        return ResponseEntity.ok(labels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelResponseTo> getLabelById(@PathVariable Long id) {
        LabelResponseTo label = labelService.getById(id);
        return ResponseEntity.ok(label);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<LabelResponseTo> getLabelByName(@PathVariable String name) {
        LabelResponseTo label = labelService.findByName(name);
        return ResponseEntity.ok(label);
    }

    @PostMapping
    public ResponseEntity<LabelResponseTo> createLabel(@Valid @RequestBody LabelRequestTo request) {
        LabelResponseTo createdLabel = labelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLabel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabelResponseTo> updateLabel(@PathVariable Long id,
                                                       @Valid @RequestBody LabelRequestTo request) {
        LabelResponseTo updatedLabel = labelService.update(id, request);
        return ResponseEntity.ok(updatedLabel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long id) {
        labelService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/page")
    public ResponseEntity<Page<LabelResponseTo>> getLabelsPage(
            @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<LabelResponseTo> labels = labelService.getAll(pageable);
        return ResponseEntity.ok(labels);
    }
}