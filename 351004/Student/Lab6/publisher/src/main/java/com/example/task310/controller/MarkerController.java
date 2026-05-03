package com.example.task310.controller;

import com.example.task310.dto.*;
import com.example.task310.service.MarkerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping({"/api/v1.0/markers", "/api/v2.0/markers"})
@RequiredArgsConstructor
public class MarkerController {
    private final MarkerService service;

    @GetMapping
    public List<MarkerResponseTo> getAll(Pageable pageable) {
        return service.getAll(pageable);
    }

    @GetMapping("/{id}")
    public MarkerResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAnonymous() or hasRole('ADMIN')")
    public MarkerResponseTo create(@Valid @RequestBody MarkerRequestTo dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAnonymous() or hasRole('ADMIN')")
    public MarkerResponseTo update(@PathVariable Long id, @Valid @RequestBody MarkerRequestTo dto) {
        dto.setId(id);
        return service.update(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("isAnonymous() or hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
