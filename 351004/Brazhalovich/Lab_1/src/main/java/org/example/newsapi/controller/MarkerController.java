package org.example.newsapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.newsapi.dto.request.MarkerRequestTo;
import org.example.newsapi.dto.response.MarkerResponseTo;
import org.example.newsapi.service.MarkerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/markers")
@RequiredArgsConstructor
public class MarkerController {

    private final MarkerService markerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarkerResponseTo create(@RequestBody @Valid MarkerRequestTo request) { // ПРОВЕРЬ @Valid
        return markerService.create(request);
    }

    @GetMapping
    public List<MarkerResponseTo> getAll(@PageableDefault(size = 50) Pageable pageable) {
        return markerService.findAll(pageable).getContent();
    }

    @GetMapping("/{id}")
    public MarkerResponseTo getById(@PathVariable Long id) {
        return markerService.findById(id);
    }

    @PutMapping("/{id}")
    public MarkerResponseTo update(@PathVariable Long id, @RequestBody @Valid MarkerRequestTo request) {
        return markerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        markerService.delete(id);
    }
}