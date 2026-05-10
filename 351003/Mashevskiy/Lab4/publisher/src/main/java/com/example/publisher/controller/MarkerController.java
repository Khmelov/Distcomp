package com.example.publisher.controller;

import com.example.publisher.entity.Marker;
import com.example.publisher.repository.MarkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1.0/markers")
public class MarkerController {

    @Autowired
    private MarkerRepository markerRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Marker createMarker(@RequestBody Marker marker) {
        return markerRepository.save(marker);
    }

    @GetMapping
    public Iterable<Marker> getAllMarkers() {
        return markerRepository.findAll();
    }

    @GetMapping("/{id}")
    public Marker getMarker(@PathVariable Long id) {
        return markerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public Marker updateMarker(@PathVariable Long id, @RequestBody Marker marker) {
        if (!markerRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        marker.setId(id);
        return markerRepository.save(marker);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMarker(@PathVariable Long id) {
        if (!markerRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Marker not found");
        }
        markerRepository.deleteById(id);
    }
}