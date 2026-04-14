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
    public ResponseEntity<MarkerResponseTo> create(@Valid @RequestBody MarkerRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(markerService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkerResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(markerService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<MarkerResponseTo>> getAll() {
        return ResponseEntity.ok(markerService.getAll());
    }

    @PutMapping
    public ResponseEntity<MarkerResponseTo> update(@Valid @RequestBody MarkerRequestTo request) {
        return ResponseEntity.ok(markerService.update(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        markerService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}