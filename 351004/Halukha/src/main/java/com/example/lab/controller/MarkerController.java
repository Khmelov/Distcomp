package com.example.lab.controller;

import java.util.List;

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

import com.example.lab.dto.MarkerRequestTo;
import com.example.lab.dto.MarkerResponseTo;
import com.example.lab.service.MarkerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1.0/markers")
public class MarkerController {

    private final MarkerService markerService;

    public MarkerController(MarkerService markerService) {
        this.markerService = markerService;
    }

    @GetMapping
    public ResponseEntity<List<MarkerResponseTo>> getAllMarker() {
        return ResponseEntity.ok(markerService.getAllMarker());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkerResponseTo> getMarker(@PathVariable Long id) {
        return ResponseEntity.ok(markerService.getMarkerById(id));
    }

    @PostMapping
    public ResponseEntity<MarkerResponseTo> createMarker(@Valid @RequestBody MarkerRequestTo marker) {
        return ResponseEntity.status(HttpStatus.CREATED).body(markerService.createMarker(marker));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarkerResponseTo> updateMarker(@PathVariable Long id, @Valid @RequestBody MarkerRequestTo marker) {
        return ResponseEntity.ok(markerService.updateMarker(id, marker));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMarker(@PathVariable Long id) {
        markerService.deleteMarker(id);
        return ResponseEntity.noContent().build();
    }
}