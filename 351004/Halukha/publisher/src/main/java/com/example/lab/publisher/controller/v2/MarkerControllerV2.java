package com.example.lab.publisher.controller.v2;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lab.publisher.dto.MarkerRequestTo;
import com.example.lab.publisher.dto.MarkerResponseTo;
import com.example.lab.publisher.service.MarkerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v2.0/markers")
public class MarkerControllerV2 {

    private final MarkerService markerService;

    public MarkerControllerV2(MarkerService markerService) {
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarkerResponseTo> createMarker(@Valid @RequestBody MarkerRequestTo marker) {
        return ResponseEntity.status(HttpStatus.CREATED).body(markerService.createMarker(marker));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarkerResponseTo> updateMarker(@PathVariable Long id, @Valid @RequestBody MarkerRequestTo marker) {
        return ResponseEntity.ok(markerService.updateMarker(id, marker));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMarker(@PathVariable Long id) {
        markerService.deleteMarker(id);
        return ResponseEntity.noContent().build();
    }
}

