package com.adashkevich.rest.lab.controller;

import com.adashkevich.rest.lab.dto.request.EditorRequestTo;
import com.adashkevich.rest.lab.dto.response.EditorResponseTo;
import com.adashkevich.rest.lab.service.EditorService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
public class EditorController {
    private final EditorService service;

    public EditorController(EditorService service) {
        this.service = service;
    }

    @PostMapping("/api/v1.0/editors")
    public ResponseEntity<EditorResponseTo> createV1(@Valid @RequestBody EditorRequestTo body) {
        return ResponseEntity.status(201).body(service.create(body));
    }

    @PostMapping("/api/v2.0/editors")
    public ResponseEntity<EditorResponseTo> registerV2(@Valid @RequestBody EditorRequestTo body) {
        return ResponseEntity.status(201).body(service.register(body));
    }

    @GetMapping({"/api/v1.0/editors", "/api/v2.0/editors"})
    public List<EditorResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping({"/api/v1.0/editors/{id}", "/api/v2.0/editors/{id}"})
    public EditorResponseTo getById(@PathVariable @Positive Long id) {
        return service.getById(id);
    }

    @PutMapping({"/api/v1.0/editors/{id}", "/api/v2.0/editors/{id}"})
    public EditorResponseTo update(@PathVariable @Positive Long id, @Valid @RequestBody EditorRequestTo body) {
        return service.update(id, body);
    }

    @DeleteMapping({"/api/v1.0/editors/{id}", "/api/v2.0/editors/{id}"})
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
