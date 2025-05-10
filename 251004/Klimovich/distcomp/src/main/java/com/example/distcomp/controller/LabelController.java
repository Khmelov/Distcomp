package com.example.distcomp.controller;

import com.example.distcomp.dto.LabelRequestTo;
import com.example.distcomp.dto.LabelResponseTo;
import com.example.distcomp.mapper.LabelMapper;
import com.example.distcomp.model.Label;
import com.example.distcomp.service.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/labels")
@RequiredArgsConstructor
public class LabelController {
    private final LabelService labelService;

    @PostMapping
    public ResponseEntity<LabelResponseTo> createLabel(@RequestBody LabelRequestTo request) {
        LabelResponseTo response = labelService.createLabel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LabelResponseTo>> getAllLabels() {
        return ResponseEntity.ok(labelService.getAllLabels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelResponseTo> getLabelById(@PathVariable Long id) {
        return ResponseEntity.ok(labelService.getLabelById(id));
    }

    @PutMapping
    public ResponseEntity<LabelResponseTo> updateLabel(@RequestBody LabelRequestTo request) {
        return ResponseEntity.ok(labelService.updateLabel(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
        return ResponseEntity.noContent().build();
    }
}
