package com.example.publisher.controller;

import com.example.publisher.entity.Marker;
import com.example.publisher.repository.MarkerRepository;
import com.example.publisher.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1.0/markers")
public class MarkerController {

    @Autowired
    private MarkerRepository markerRepository;

    @Autowired
    private CacheService cacheService;

    private static final String CACHE_KEY_PREFIX = "marker:";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Marker createMarker(@RequestBody Marker marker) {
        Marker saved = markerRepository.save(marker);
        cacheService.put(CACHE_KEY_PREFIX + saved.getId(), saved);
        return saved;
    }

    @GetMapping
    public Iterable<Marker> getAllMarkers() {
        return markerRepository.findAll();
    }

    @GetMapping("/{id}")
    public Marker getMarker(@PathVariable Long id) {
        String cacheKey = CACHE_KEY_PREFIX + id;
        Marker cached = (Marker) cacheService.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        Marker marker = markerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        cacheService.put(cacheKey, marker);
        return marker;
    }

    @PutMapping("/{id}")
    public Marker updateMarker(@PathVariable Long id, @RequestBody Marker marker) {
        if (!markerRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        marker.setId(id);
        Marker updated = markerRepository.save(marker);
        cacheService.put(CACHE_KEY_PREFIX + id, updated);
        return updated;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMarker(@PathVariable Long id) {
        if (!markerRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Marker not found");
        }
        markerRepository.deleteById(id);
        cacheService.evict(CACHE_KEY_PREFIX + id);
    }
}