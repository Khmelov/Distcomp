package com.example.restApi.controllers;

import com.example.restApi.dto.request.MarkerRequestTo;
import com.example.restApi.dto.response.MarkerResponseTo;
import com.example.restApi.services.MarkerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/markers")
public class MarkerController {
    private final MarkerService markerService;

    public MarkerController(MarkerService markerService) {
        this.markerService = markerService;
    }

    @GetMapping
    public ResponseEntity<List<MarkerResponseTo>> getAll() {
        return ResponseEntity.ok(markerService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkerResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(markerService.getById(id));
    }

    @PostMapping
    public ResponseEntity<MarkerResponseTo> create(@Valid @RequestBody MarkerRequestTo request) {
        MarkerResponseTo response = markerService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarkerResponseTo> update(@PathVariable Long id,
                                                   @Valid @RequestBody MarkerRequestTo request) {
        return ResponseEntity.ok(markerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        markerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
