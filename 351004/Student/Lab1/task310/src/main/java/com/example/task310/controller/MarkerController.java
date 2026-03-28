package com.example.task310.controller;

import com.example.task310.dto.MarkerRequestTo;
import com.example.task310.dto.MarkerResponseTo;
import com.example.task310.service.MarkerService;
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

    @GetMapping
    public List<MarkerResponseTo> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public MarkerResponseTo getById(@PathVariable Long id) { return service.getById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarkerResponseTo create(@Valid @RequestBody MarkerRequestTo dto) {
        return service.create(dto);
    }

    @PutMapping
    public MarkerResponseTo update(@Valid @RequestBody MarkerRequestTo dto) {
        return service.update(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}