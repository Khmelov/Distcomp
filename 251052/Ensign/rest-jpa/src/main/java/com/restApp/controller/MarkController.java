package com.restApp.controller;

import com.restApp.dto.MarkRequestTo;
import com.restApp.dto.MarkResponseTo;
import com.restApp.service.MarkService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0/marks")
public class MarkController {

    private final MarkService markService;

    public MarkController(MarkService markService) {
        this.markService = markService;
    }

    @PostMapping
    public ResponseEntity<MarkResponseTo> create(@RequestBody @Valid MarkRequestTo request) {
        return new ResponseEntity<>(markService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<java.util.List<MarkResponseTo>> getAll() {
        return ResponseEntity.ok(markService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(markService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarkResponseTo> update(@PathVariable Long id, @RequestBody @Valid MarkRequestTo request) {
        return ResponseEntity.ok(markService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        markService.delete(id);
    }
}
