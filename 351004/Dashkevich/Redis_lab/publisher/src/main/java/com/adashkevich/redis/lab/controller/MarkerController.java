package com.adashkevich.redis.lab.controller;

import com.adashkevich.redis.lab.dto.request.MarkerRequestTo;
import com.adashkevich.redis.lab.dto.response.MarkerResponseTo;
import com.adashkevich.redis.lab.service.MarkerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/markers")
@Validated
public class MarkerController {
    private final MarkerService service;

    public MarkerController(MarkerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<MarkerResponseTo> create(@Valid @RequestBody MarkerRequestTo body) {
        return ResponseEntity.status(201).body(service.create(body));
    }

    @GetMapping
    public List<MarkerResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MarkerResponseTo getById(@PathVariable @Positive Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public MarkerResponseTo update(@PathVariable @Positive Long id, @Valid @RequestBody MarkerRequestTo body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable @Positive Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
