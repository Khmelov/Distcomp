package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.MarkerRequestTo;
import by.bsuir.distcomp.dto.response.MarkerResponseTo;
import by.bsuir.distcomp.core.service.MarkerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/markers")
public class MarkerRestController {
    private final MarkerService markerService;

    public MarkerRestController(MarkerService markerService) {
        this.markerService = markerService;
    }

    @PostMapping
    public ResponseEntity<MarkerResponseTo> create(@Valid @RequestBody MarkerRequestTo createRequest) {
        MarkerResponseTo createdMarker = markerService.create(createRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdMarker);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkerResponseTo> getById(@PathVariable("id") Long markerId) {
        MarkerResponseTo marker = markerService.getById(markerId);
        return ResponseEntity
                .ok(marker);
    }

    @GetMapping
    public ResponseEntity<List<MarkerResponseTo>> getAll() {
        List<MarkerResponseTo> markers = markerService.getAll();
        return ResponseEntity
                .ok(markers);
    }

    @PutMapping
    public ResponseEntity<MarkerResponseTo> update(@Valid @RequestBody MarkerRequestTo updateRequest) {
        MarkerResponseTo updatedMarker = markerService.update(updateRequest);
        return ResponseEntity
                .ok(updatedMarker);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long markerId) {
        markerService.deleteById(markerId);
        return ResponseEntity
                .noContent()
                .build();
    }

    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<MarkerResponseTo>> getMarkersByTweetId(@PathVariable("tweetId") Long tweetId) {
        List<MarkerResponseTo> markers = markerService.getMarkersByTweetId(tweetId);
        return ResponseEntity
                .ok(markers);
    }
}
