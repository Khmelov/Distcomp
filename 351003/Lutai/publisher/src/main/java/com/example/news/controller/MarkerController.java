package com.example.news.controller;

import com.example.common.dto.MarkerRequestTo;
import com.example.common.dto.MarkerResponseTo;
import com.example.news.service.MarkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/v1.0/markers", "/api/v2.0/markers"})
@RequiredArgsConstructor
public class MarkerController {

    private final MarkerService markerService;

    @GetMapping
    public ResponseEntity<List<MarkerResponseTo>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(markerService.findAll(page, size, sortBy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkerResponseTo> findById(@PathVariable Long id) {
        return ResponseEntity.ok(markerService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MarkerResponseTo> create(@Valid @RequestBody MarkerRequestTo request) {
        return new ResponseEntity<>(markerService.create(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarkerResponseTo> update(
            @PathVariable Long id,
            @Valid @RequestBody MarkerRequestTo request) {
        return ResponseEntity.ok(markerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        markerService.delete(id);
    }
}