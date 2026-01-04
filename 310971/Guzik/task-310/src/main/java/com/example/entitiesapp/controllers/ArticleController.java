package com.example.entitiesapp.controllers;

import com.example.entitiesapp.dto.request.ArticleRequestTo;
import com.example.entitiesapp.dto.response.ArticleResponseTo;
import com.example.entitiesapp.services.ArticleService;
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
    public ResponseEntity<List<ArticleResponseTo>> getAllArticles() {
        List<ArticleResponseTo> articles = articleService.getAll();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponseTo> getArticleById(@PathVariable Long id) {
        ArticleResponseTo article = articleService.getById(id);
        return ResponseEntity.ok(article);
    }

    @PostMapping
    public ResponseEntity<ArticleResponseTo> createArticle(@Valid @RequestBody ArticleRequestTo articleRequestTo) {
        ArticleResponseTo createdArticle = articleService.create(articleRequestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponseTo> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleRequestTo articleRequestTo) {
        ArticleResponseTo updatedArticle = articleService.update(id, articleRequestTo);
        return ResponseEntity.ok(updatedArticle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}