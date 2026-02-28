package com.example.restApi.controllers;

import com.example.restApi.dto.request.MarkerRequestTo;
import com.example.restApi.dto.response.MarkerResponseTo;
import com.example.restApi.services.MarkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/markers")
@RequiredArgsConstructor
public class MarkerController {

    private final MarkerService markerService;

    @PostMapping
    public ResponseEntity<MarkerResponseTo> create(@Valid @RequestBody MarkerRequestTo requestTo) {
        MarkerResponseTo response = markerService.create(requestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkerResponseTo> getById(@PathVariable Long id) {
        MarkerResponseTo response = markerService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<MarkerResponseTo>> getAll() {
        List<MarkerResponseTo> markers = markerService.getAll();
        return ResponseEntity.ok(markers);
    }

    @PutMapping
    public ResponseEntity<MarkerResponseTo> update(@Valid @RequestBody MarkerRequestTo requestTo) {
        MarkerResponseTo response = markerService.update(requestTo);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        markerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
