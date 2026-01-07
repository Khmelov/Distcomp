package com.task310.blogplatform.controller;

import com.task310.blogplatform.dto.ArticleRequestTo;
import com.task310.blogplatform.dto.ArticleResponseTo;
import com.task310.blogplatform.dto.LabelResponseTo;
import com.task310.blogplatform.dto.UserResponseTo;
import com.task310.blogplatform.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0")
public class ArticleController {
    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping("/articles")
    public ResponseEntity<ArticleResponseTo> createArticle(@RequestBody ArticleRequestTo articleRequestTo) {
        ArticleResponseTo created = articleService.create(articleRequestTo);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/articles")
    public ResponseEntity<List<ArticleResponseTo>> getAllArticles(
            @RequestParam(required = false) String labelName,
            @RequestParam(required = false) List<Long> labelIds,
            @RequestParam(required = false) String userLogin,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content) {
        if (labelName != null || labelIds != null || userLogin != null || title != null || content != null) {
            List<ArticleResponseTo> articles = articleService.findByFilters(labelName, labelIds, userLogin, title, content);
            return ResponseEntity.ok(articles);
        }
        List<ArticleResponseTo> articles = articleService.findAll();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/articles/{id}")
    public ResponseEntity<ArticleResponseTo> getArticleById(@PathVariable Long id) {
        ArticleResponseTo article = articleService.findById(id);
        return ResponseEntity.ok(article);
    }

    @PutMapping("/articles/{id}")
    public ResponseEntity<ArticleResponseTo> updateArticle(@PathVariable Long id, @RequestBody ArticleRequestTo articleRequestTo) {
        ArticleResponseTo updated = articleService.update(id, articleRequestTo);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/articles/{id}/user")
    public ResponseEntity<UserResponseTo> getUserByArticleId(@PathVariable Long id) {
        UserResponseTo user = articleService.getUserByArticleId(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/articles/{id}/labels")
    public ResponseEntity<List<LabelResponseTo>> getLabelsByArticleId(@PathVariable Long id) {
        List<LabelResponseTo> labels = articleService.getLabelsByArticleId(id);
        return ResponseEntity.ok(labels);
    }
}

