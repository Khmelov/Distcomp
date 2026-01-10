package com.blog.controller;

import com.blog.dto.ArticleRequestTo;
import com.blog.dto.ArticleResponseTo;
import com.blog.service.ArticleService;
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
    public ResponseEntity<List<ArticleResponseTo>> getAllArticles() {
        List<ArticleResponseTo> articles = articleService.findAll();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponseTo> getArticleById(@PathVariable Long id) {
        ArticleResponseTo article = articleService.findById(id);
        return ResponseEntity.ok(article);
    }

    @PostMapping
    public ResponseEntity<ArticleResponseTo> createArticle(@Valid @RequestBody ArticleRequestTo request) {
        ArticleResponseTo createdArticle = articleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponseTo> updateArticle(@PathVariable Long id, @Valid @RequestBody ArticleRequestTo request) {
        ArticleResponseTo updatedArticle = articleService.update(id, request);
        return ResponseEntity.ok(updatedArticle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}