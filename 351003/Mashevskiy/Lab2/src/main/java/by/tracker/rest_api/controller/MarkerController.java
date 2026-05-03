package by.tracker.rest_api.controller;

import by.tracker.rest_api.entity.Marker;
import by.tracker.rest_api.repository.MarkerRepository;
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
    private MarkerRepository markerRepository;

    @GetMapping
    public List<Marker> getAll() {
        return markerRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Marker> getById(@PathVariable Long id) {
        return markerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Marker marker) {
        if (marker.getName() == null || marker.getName().length() < 2 || marker.getName().length() > 32) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorMessage", "Name must be between 2 and 32 characters");
            error.put("errorCode", 40016);
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(error);
        }

        if (markerRepository.existsByName(marker.getName())) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorMessage", "Marker with this name already exists");
            error.put("errorCode", 40902);
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        Marker saved = markerRepository.save(marker);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Marker marker) {
        Marker existingMarker = markerRepository.findById(id).orElse(null);
        if (existingMarker == null) {
            return ResponseEntity.notFound().build();
        }

        if (marker.getName() == null || marker.getName().length() < 2 || marker.getName().length() > 32) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorMessage", "Name must be between 2 and 32 characters");
            error.put("errorCode", 40016);
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(error);
        }

        if (!marker.getName().equals(existingMarker.getName()) &&
                markerRepository.existsByName(marker.getName())) {
            Map<String, Object> error = new HashMap<>();
            error.put("errorMessage", "Marker with this name already exists");
            error.put("errorCode", 40902);
            error.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
        }

        existingMarker.setName(marker.getName());
        Marker saved = markerRepository.save(existingMarker);
        return ResponseEntity.ok(saved);
    }
    @DeleteMapping("/byName/{name}")
    public ResponseEntity<Void> deleteByName(@PathVariable String name) {
        markerRepository.deleteByName(name);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!markerRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        markerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}