package org.rv.lab1.controller;

import jakarta.validation.Valid;
import org.rv.lab1.api.ApiPaths;
import org.rv.lab1.dto.MarkerRequestTo;
import org.rv.lab1.dto.MarkerResponseTo;
import org.rv.lab1.service.MarkerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.API_V1 + "/markers")
public class MarkerController {
    private final MarkerService service;

    public MarkerController(MarkerService service) {
        this.service = service;
    }

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
    public MarkerResponseTo getById(@PathVariable long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public MarkerResponseTo update(@PathVariable long id, @Valid @RequestBody MarkerRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}

