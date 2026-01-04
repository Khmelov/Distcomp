package com.task310.socialnetwork.controller;

import com.task310.socialnetwork.dto.request.LabelRequestTo;
import com.task310.socialnetwork.dto.response.LabelResponseTo;
import com.task310.socialnetwork.service.LabelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    @ResponseStatus(HttpStatus.OK)
    public List<LabelResponseTo> getAllLabels() {
        return labelService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelResponseTo> getLabelById(@PathVariable Long id) {
        LabelResponseTo label = labelService.getById(id);
        return ResponseEntity.ok(label);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseTo createLabel(@Valid @RequestBody LabelRequestTo request) {
        return labelService.create(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabelResponseTo> updateLabel(@PathVariable Long id,
                                                       @Valid @RequestBody LabelRequestTo request) {
        LabelResponseTo updatedLabel = labelService.update(id, request);
        return ResponseEntity.ok(updatedLabel);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLabel(@PathVariable Long id) {
        labelService.delete(id);
    }
}