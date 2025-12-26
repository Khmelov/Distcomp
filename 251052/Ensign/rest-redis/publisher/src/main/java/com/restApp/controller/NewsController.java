package com.restApp.controller;

import com.restApp.dto.NewsRequestTo;
import com.restApp.dto.NewsResponseTo;
import com.restApp.service.NewsService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1.0/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @PostMapping
    public ResponseEntity<NewsResponseTo> create(@RequestBody @Valid NewsRequestTo request) {
        return new ResponseEntity<>(newsService.create(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<java.util.List<NewsResponseTo>> getAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(newsService.findAll(pageable).getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsResponseTo> update(@PathVariable Long id, @RequestBody @Valid NewsRequestTo request) {
        return ResponseEntity.ok(newsService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        newsService.delete(id);
    }
}
