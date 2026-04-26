package com.example.Task310.controller;

import com.example.Task310.dto.MarkerRequestTo;
import com.example.Task310.dto.MarkerResponseTo;
import com.example.Task310.service.MarkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/markers")
@RequiredArgsConstructor
public class MarkerController {
    private final MarkerService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarkerResponseTo create(@Valid @RequestBody MarkerRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<MarkerResponseTo> findAll(Pageable pageable) {
        return service.findAll(pageable);
    }

    @GetMapping("/{id}")
    public MarkerResponseTo findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public MarkerResponseTo update(@PathVariable Long id, @Valid @RequestBody MarkerRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}