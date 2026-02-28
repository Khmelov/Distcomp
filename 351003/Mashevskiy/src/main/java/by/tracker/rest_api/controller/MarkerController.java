package by.tracker.rest_api.controller;

import by.tracker.rest_api.dto.MarkerRequestTo;
import by.tracker.rest_api.dto.MarkerResponseTo;
import by.tracker.rest_api.service.MarkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/markers")
@RequiredArgsConstructor
public class MarkerController {

    private final MarkerService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarkerResponseTo create(@Valid @RequestBody MarkerRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<MarkerResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public MarkerResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping
    public MarkerResponseTo update(@Valid @RequestBody MarkerRequestTo request) {
        return service.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}