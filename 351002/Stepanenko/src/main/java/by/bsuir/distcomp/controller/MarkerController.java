package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.MarkerRequestTo;
import by.bsuir.distcomp.dto.response.MarkerResponseTo;
import by.bsuir.distcomp.service.MarkerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/markers")
public class MarkerController {
    private final MarkerService markerService;

    public MarkerController(MarkerService markerService) {
        this.markerService = markerService;
    }

    @PostMapping
    public ResponseEntity<MarkerResponseTo> create(@Valid @RequestBody MarkerRequestTo dto) {
        MarkerResponseTo response = markerService.create(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkerResponseTo> getById(@PathVariable Long id) {
        MarkerResponseTo response = markerService.getById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<MarkerResponseTo>> getAll() {
        List<MarkerResponseTo> response = markerService.getAll();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<MarkerResponseTo> update(@Valid @RequestBody MarkerRequestTo dto) {
        MarkerResponseTo response = markerService.update(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        markerService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/tweet/{tweetId}")
    public ResponseEntity<List<MarkerResponseTo>> getMarkersByTweetId(@PathVariable Long tweetId) {
        List<MarkerResponseTo> response = markerService.getMarkersByTweetId(tweetId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
