package com.example.kafkademo.controller.v2;

import com.example.kafkademo.dto.request.MarkerRequestDto;
import com.example.kafkademo.dto.response.MarkerResponseDto;
import com.example.kafkademo.entity.Marker;
import com.example.kafkademo.service.MarkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2.0/markers")
public class MarkerControllerV2 {

    @Autowired
    private MarkerService markerService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public List<MarkerResponseDto> getAll() {
        return markerService.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<MarkerResponseDto> getById(@PathVariable Long id) {
        return markerService.findById(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-name/{name}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<MarkerResponseDto> getByName(@PathVariable String name) {
        return markerService.findByName(name)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarkerResponseDto> create(@Valid @RequestBody MarkerRequestDto request) {
        if (markerService.existsByName(request.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Marker marker = new Marker();
        marker.setName(request.getName());
        Marker saved = markerService.save(marker);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MarkerResponseDto> update(@PathVariable Long id, @Valid @RequestBody MarkerRequestDto request) {
        if (!markerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        Marker marker = markerService.findById(id).get();
        marker.setName(request.getName());
        Marker updated = markerService.save(marker);
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!markerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        markerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private MarkerResponseDto toDto(Marker marker) {
        MarkerResponseDto dto = new MarkerResponseDto();
        dto.setId(marker.getId());
        dto.setName(marker.getName());
        return dto;
    }
}