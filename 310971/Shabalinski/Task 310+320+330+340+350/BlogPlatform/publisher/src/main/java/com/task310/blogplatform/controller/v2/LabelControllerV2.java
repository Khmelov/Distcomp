package com.task310.blogplatform.controller.v2;

import com.task310.blogplatform.dto.LabelRequestTo;
import com.task310.blogplatform.dto.LabelResponseTo;
import com.task310.blogplatform.service.CurrentUserService;
import com.task310.blogplatform.service.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0")
public class LabelControllerV2 {
    private final LabelService labelService;
    private final CurrentUserService currentUserService;

    @Autowired
    public LabelControllerV2(LabelService labelService, CurrentUserService currentUserService) {
        this.labelService = labelService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/labels")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LabelResponseTo> createLabel(@RequestBody LabelRequestTo labelRequestTo) {
        LabelResponseTo created = labelService.create(labelRequestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/labels")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<List<LabelResponseTo>> getAllLabels() {
        List<LabelResponseTo> labels = labelService.findAll();
        return ResponseEntity.ok(labels);
    }

    @GetMapping("/labels/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<LabelResponseTo> getLabelById(@PathVariable Long id) {
        LabelResponseTo label = labelService.findById(id);
        return ResponseEntity.ok(label);
    }

    @PutMapping("/labels/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LabelResponseTo> updateLabel(@PathVariable Long id, @RequestBody LabelRequestTo labelRequestTo) {
        LabelResponseTo updated = labelService.update(id, labelRequestTo);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/labels/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long id) {
        labelService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}

