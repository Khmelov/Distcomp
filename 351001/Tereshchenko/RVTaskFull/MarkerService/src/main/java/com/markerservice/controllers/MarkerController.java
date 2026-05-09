package com.markerservice.controllers;

import com.markerservice.dtos.MarkerRequestTo;
import com.markerservice.dtos.MarkerResponseByNameTo;
import com.markerservice.dtos.MarkerResponseTo;
import com.markerservice.dtos.TweetMarkersRequestByNameTo;
import com.markerservice.services.MarkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1.0")
public class MarkerController {

    private final MarkerService markerService;

    @PostMapping("/markers")
    public ResponseEntity<MarkerResponseTo> createMarker(@Valid @RequestBody MarkerRequestTo request) {
        return new ResponseEntity<>(markerService.createMarker(request), HttpStatus.CREATED);
    }

    @GetMapping("/markers")
    public ResponseEntity<List<MarkerResponseTo>> getAllMarkers() {
        return ResponseEntity.ok(markerService.findAllMarkers());
    }

    @PostMapping("/markers/markersNames")
    public ResponseEntity<List<MarkerResponseByNameTo>> getMarkersByNames(
            @RequestBody List<TweetMarkersRequestByNameTo> request) {
        return ResponseEntity.ok(markerService.createValidMarkersByNames(request));
    }

    @GetMapping("/markers/{id}")
    public ResponseEntity<MarkerResponseTo> getMarkerById(@PathVariable Long id) {
        return ResponseEntity.ok(markerService.findMarkerById(id));
    }

    @GetMapping(value = "/markers", params = "ids")
    public ResponseEntity<List<MarkerResponseTo>> getMarkersByIds(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(markerService.findAllMarkersByIds(ids));
    }

    @PutMapping("/markers/{id}")
    public ResponseEntity<MarkerResponseTo> updateMarkerById(@Valid @RequestBody MarkerRequestTo request, @PathVariable Long id) {
        return ResponseEntity.ok(markerService.updateMarkerById(request, id));
    }

    @DeleteMapping("/markers/{id}")
    public ResponseEntity<Void> deleteMarkerById(@PathVariable Long id) {
        markerService.deleteMarkerById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/markers/ids")
    public ResponseEntity<Void> deleteMarkersByIds(@RequestBody List<Long> ids) {
        markerService.deleteMarkersByIds(ids);
        return ResponseEntity.noContent().build();
    }
}
