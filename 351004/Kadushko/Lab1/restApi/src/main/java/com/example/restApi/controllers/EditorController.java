package com.example.restApi.controllers;

import com.example.restApi.dto.request.EditorRequestTo;
import com.example.restApi.dto.response.EditorResponseTo;
import com.example.restApi.services.EditorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/editors")
public class EditorController {

    private final EditorService editorService;

    public EditorController(EditorService editorService) {
        this.editorService = editorService;
    }

    @GetMapping
    public ResponseEntity<List<EditorResponseTo>> getAll() {
        return ResponseEntity.ok(editorService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EditorResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(editorService.getById(id));
    }

    @PostMapping
    public ResponseEntity<EditorResponseTo> create(@Valid @RequestBody EditorRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(editorService.create(request));
    }

    @PutMapping
    public ResponseEntity<EditorResponseTo> update(@Valid @RequestBody EditorRequestTo request) {
        return ResponseEntity.ok(editorService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        editorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
