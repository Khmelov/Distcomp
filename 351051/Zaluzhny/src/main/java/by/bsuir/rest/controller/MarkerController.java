package by.bsuir.rest.controller;

import by.bsuir.rest.dto.request.MarkerRequestTo;
import by.bsuir.rest.dto.response.MarkerResponseTo;
import by.bsuir.rest.service.MarkerService;
import by.bsuir.rest.web.ApiPaths;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.MARKERS)
@RequiredArgsConstructor
public class MarkerController {

    private final MarkerService markerService;

    @PostMapping
    public ResponseEntity<MarkerResponseTo> create(@RequestBody MarkerRequestTo request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(markerService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<MarkerResponseTo>> getAll() {
        return ResponseEntity.ok(markerService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MarkerResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(markerService.getById(id));
    }

    @PutMapping
    public ResponseEntity<MarkerResponseTo> update(@RequestBody MarkerRequestTo request) {
        return ResponseEntity.ok(markerService.update(null, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MarkerResponseTo> update(@PathVariable Long id, @RequestBody MarkerRequestTo request) {
        return ResponseEntity.ok(markerService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        markerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
