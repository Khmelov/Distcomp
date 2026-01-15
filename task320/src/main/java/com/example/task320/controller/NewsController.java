package com.example.task320.controller;

import com.example.task320.dto.request.NewsRequestTo;
import com.example.task320.dto.response.NewsResponseTo;
import com.example.task320.service.NewsService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/news")
public class NewsController {

    private final NewsService service;

    public NewsController(NewsService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<NewsResponseTo> create(@Valid @RequestBody NewsRequestTo body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(body));
    }

    @GetMapping
    public List<NewsResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public NewsResponseTo getById(@PathVariable long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public NewsResponseTo update(@PathVariable long id, @Valid @RequestBody NewsRequestTo body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        service.delete(id);
    }
}
