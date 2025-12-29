package com.rest.controller;

import com.rest.dto.request.LabelRequestTo;
import com.rest.dto.response.LabelResponseTo;
import com.rest.service.LabelService;
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
    private final LabelService labelService;
    
    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }
    
    @PostMapping
    public ResponseEntity<LabelResponseTo> create(@Valid @RequestBody LabelRequestTo request) {
        LabelResponseTo response = labelService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LabelResponseTo> findById(@PathVariable Long id) {
        LabelResponseTo response = labelService.findById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<LabelResponseTo>> findAll() {
        List<LabelResponseTo> responses = labelService.findAll();
        return ResponseEntity.ok(responses);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<LabelResponseTo> update(@Valid @PathVariable Long id, 
                                                  @RequestBody LabelRequestTo request) {
        LabelResponseTo response = labelService.update(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        labelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}