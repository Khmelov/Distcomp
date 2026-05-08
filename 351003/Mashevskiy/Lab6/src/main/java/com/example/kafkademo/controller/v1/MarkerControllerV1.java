package com.example.kafkademo.controller.v1;

import com.example.kafkademo.entity.Marker;
import com.example.kafkademo.service.MarkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/markers")
public class MarkerControllerV1 {

    @Autowired
    private MarkerService markerService;

    @GetMapping
    public List<Marker> getAll() {
        return markerService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Marker> getById(@PathVariable Long id) {
        return markerService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<Marker> getByName(@PathVariable String name) {
        return markerService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Marker> create(@RequestBody Marker marker) {
        if (markerService.existsByName(marker.getName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(markerService.save(marker));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Marker> update(@PathVariable Long id, @RequestBody Marker marker) {
        if (!markerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        marker.setId(id);
        return ResponseEntity.ok(markerService.save(marker));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!markerService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        markerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}