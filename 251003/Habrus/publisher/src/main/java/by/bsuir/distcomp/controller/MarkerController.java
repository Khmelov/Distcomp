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
@RequestMapping(value = "/api/v1.0/markers")
public class MarkerController {
    private final MarkerService markerService;

    public MarkerController(MarkerService markerService) {
        this.markerService = markerService;
    }

    @GetMapping
    ResponseEntity<List<MarkerResponseTo>> getAllMarkers() {
        return new ResponseEntity<>(markerService.getAllMarkers(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id:\\d+}")
    ResponseEntity<MarkerResponseTo> getMarkerById(@PathVariable Long id) {
        return new ResponseEntity<>(markerService.getMarkerById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<MarkerResponseTo> createMarker(@RequestBody @Valid MarkerRequestTo markerRequestTo) {
        return new ResponseEntity<>(markerService.createMarker(markerRequestTo), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<MarkerResponseTo> updateMarker(@RequestBody @Valid MarkerRequestTo markerRequestTo) {
        return new ResponseEntity<>(markerService.updateMarker(markerRequestTo), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id:\\d+}")
    public ResponseEntity<MarkerResponseTo> deleteMarker(@PathVariable Long id) {
        markerService.deleteMarker(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
