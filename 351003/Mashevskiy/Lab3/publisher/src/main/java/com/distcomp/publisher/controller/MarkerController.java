package com.distcomp.publisher.controller;

import com.distcomp.publisher.dto.MarkerRequestDTO;
import com.distcomp.publisher.dto.MarkerResponseDTO;
import com.distcomp.publisher.service.MarkerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1.0/markers")
public class MarkerController {

    @Autowired
    private MarkerService markerService;

    @GetMapping
    public ResponseEntity<List<MarkerResponseDTO>> getAll() {
        return ResponseEntity.ok(markerService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkerResponseDTO> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(markerService.getById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<MarkerResponseDTO> create(@Valid @RequestBody MarkerRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(markerService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarkerResponseDTO> update(@PathVariable Long id, @Valid @RequestBody MarkerRequestDTO dto) {
        try {
            return ResponseEntity.ok(markerService.update(id, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        try {
            markerService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Marker not found with id: " + id);
            errorResponse.put("status", "404");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}