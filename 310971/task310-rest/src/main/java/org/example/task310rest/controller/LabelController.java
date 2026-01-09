package org.example.task310rest.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.example.task310rest.dto.LabelRequestTo;
import org.example.task310rest.dto.LabelResponseTo;
import org.example.task310rest.service.LabelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1.0/labels")
public class LabelController {

    private final LabelService service;

    public LabelController(LabelService service) {
        this.service = service;
    }

    @GetMapping
    public List<LabelResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public LabelResponseTo getById(@PathVariable("id") Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<LabelResponseTo> create(@Valid @RequestBody LabelRequestTo request) {
        LabelResponseTo response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public LabelResponseTo update(@PathVariable("id") Long id, @Valid @RequestBody LabelRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}


