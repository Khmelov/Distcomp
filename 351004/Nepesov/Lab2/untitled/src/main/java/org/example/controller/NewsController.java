package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.NewsRequestTo;
import org.example.dto.NewsResponseTo;
import org.example.service.NewsService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewsResponseTo create(@Valid @RequestBody NewsRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<NewsResponseTo> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return service.findAll(page, size, sortBy);
    }

    @GetMapping("/{id}")
    public NewsResponseTo findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping
    public NewsResponseTo update(@Valid @RequestBody NewsRequestTo request) {
        return service.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}