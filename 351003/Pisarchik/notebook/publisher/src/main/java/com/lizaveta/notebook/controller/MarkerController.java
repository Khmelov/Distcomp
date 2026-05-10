package com.lizaveta.notebook.controller;

import com.lizaveta.notebook.model.dto.request.MarkerRequestTo;
import com.lizaveta.notebook.model.dto.response.MarkerResponseTo;
import com.lizaveta.notebook.model.dto.response.PageResponseTo;
import com.lizaveta.notebook.service.MarkerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/markers")
public class MarkerController {

    private final MarkerService markerService;

    public MarkerController(final MarkerService markerService) {
        this.markerService = markerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarkerResponseTo create(@Valid @RequestBody final MarkerRequestTo request) {
        return markerService.create(request);
    }

    @GetMapping
    public Object findAll(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size,
            @RequestParam(required = false) final String sortBy,
            @RequestParam(defaultValue = "asc") final String sortOrder,
            @RequestParam(value = "format", required = false) final String format) {
        boolean wantList = "list".equalsIgnoreCase(format)
                || (format == null && page == 0 && size == 20 && (sortBy == null || sortBy.isBlank()));
        if (wantList) {
            return markerService.findAll();
        }
        return markerService.findAll(page, size, sortBy, sortOrder);
    }

    @GetMapping("/{id}")
    public MarkerResponseTo findById(@PathVariable final Long id) {
        return markerService.findById(id);
    }

    @PutMapping("/{id}")
    public MarkerResponseTo update(
            @PathVariable final Long id,
            @Valid @RequestBody final MarkerRequestTo request) {
        return markerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable final Long id) {
        markerService.deleteById(id);
    }
}
