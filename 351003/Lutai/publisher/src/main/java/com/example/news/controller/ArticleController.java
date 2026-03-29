package com.example.news.controller;

import com.example.common.dto.ArticleRequestTo;
import com.example.common.dto.ArticleResponseTo;
import com.example.common.dto.MarkerResponseTo;
import com.example.common.dto.MessageResponseTo;
import com.example.news.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<List<ArticleResponseTo>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        return ResponseEntity.ok(articleService.findAll(page, size, sortBy));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponseTo> getById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleResponseTo create(@Valid @RequestBody ArticleRequestTo request) {
        return articleService.create(request);
    }

    @PutMapping("/{id}")
    public ArticleResponseTo update(@PathVariable Long id, @Valid @RequestBody ArticleRequestTo request) {
        return articleService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        articleService.delete(id);
    }

    @GetMapping("/{id}/markers")
    public List<MarkerResponseTo> getMarkersByArticleId(@PathVariable Long id) {
        return articleService.getMarkersByArticleId(id);
    }
}