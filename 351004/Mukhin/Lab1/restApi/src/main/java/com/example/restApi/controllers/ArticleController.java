package com.example.restApi.controllers;

import com.example.restApi.dto.request.ArticleRequestTo;
import com.example.restApi.dto.response.ArticleResponseTo;
import com.example.restApi.services.ArticleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/articles")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ResponseEntity<List<ArticleResponseTo>> getAll() {
        return ResponseEntity.ok(articleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ArticleResponseTo> create(@Valid @RequestBody ArticleRequestTo request) {
        ArticleResponseTo response = articleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponseTo> update(@PathVariable Long id,
                                                    @Valid @RequestBody ArticleRequestTo request) {
        return ResponseEntity.ok(articleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
