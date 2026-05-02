package com.example.demo.controller;

import com.example.demo.dto.NewsRequest;
import com.example.demo.entity.News;
import com.example.demo.exception.NotFoundException;
import com.example.demo.service.NewsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;
import java.util.List;

@RestController
@RequestMapping("/api/v2.0/news")
@Profile("docker")
public class NewsControllerV2 {

    private final NewsService newsService;

    public NewsControllerV2(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<List<News>> getAll() {
        return ResponseEntity.ok(newsService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<News> getById(@PathVariable Long id) {
        return newsService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("News not found with id: " + id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<News> create(@RequestBody NewsRequest request) {
        News created = newsService.createFromRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @ownershipValidator.isNewsOwner(#id, authentication.name))")
    public ResponseEntity<News> update(@PathVariable Long id, @RequestBody NewsRequest request) {
        return ResponseEntity.ok(newsService.updateFromRequest(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @ownershipValidator.isNewsOwner(#id, authentication.name))")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        newsService.delete(id);
        return ResponseEntity.noContent().build();
    }
}