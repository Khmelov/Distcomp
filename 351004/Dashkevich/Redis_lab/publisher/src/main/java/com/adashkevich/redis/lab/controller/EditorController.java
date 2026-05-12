package com.adashkevich.redis.lab.controller;

import com.adashkevich.redis.lab.dto.request.EditorRequestTo;
import com.adashkevich.redis.lab.dto.response.EditorResponseTo;
import com.adashkevich.redis.lab.service.EditorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/editors")
@Validated
public class EditorController {
    private final EditorService service;

    public EditorController(EditorService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EditorResponseTo> create(@Valid @RequestBody EditorRequestTo body) {
        return ResponseEntity.status(201).body(service.create(body));
    }

    @GetMapping
    public List<EditorResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public EditorResponseTo getById(@PathVariable @Positive Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public EditorResponseTo update(@PathVariable @Positive Long id, @Valid @RequestBody EditorRequestTo body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
