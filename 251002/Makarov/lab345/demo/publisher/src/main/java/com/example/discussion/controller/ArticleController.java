package com.example.discussion.controller;


import com.example.discussion.dto.ArticleRequestTo;
import com.example.discussion.dto.ArticleResponseTo;
import com.example.discussion.service.ArticleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/articles")
public class ArticleController {

    private final ArticleService articlesService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public ArticleController(ArticleService articlesService, RedisTemplate<String, Object> redisTemplate) {
        this.articlesService = articlesService;
        this.redisTemplate = redisTemplate;
    }

    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @GetMapping
    public ResponseEntity<?> getAllArticles() {
        try {
            String cacheKey = "allArticles";
            List<ArticleResponseTo> articlesList = (List<ArticleResponseTo>) redisTemplate.opsForValue().get(cacheKey);

            if (articlesList == null) {
                articlesList = articlesService.findAll();
                redisTemplate.opsForValue().set(cacheKey, articlesList, Duration.ofMinutes(10));
            }
            return new ResponseEntity<>(articlesList, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving articles list", e);  // Логирование ошибки
            return new ResponseEntity<>("Error retrieving articles list", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getArticleById(@PathVariable Long id) {
        try {
            String cacheKey = "articles:" + id;
            ArticleResponseTo articles = (ArticleResponseTo) redisTemplate.opsForValue().get(cacheKey);

            if (articles == null) {
                articles = articlesService.findById(id);
                if (articles != null) {
                    redisTemplate.opsForValue().set(cacheKey, articles, Duration.ofMinutes(10));
                } else {
                    return new ResponseEntity<>("Articles not found", HttpStatus.NOT_FOUND);
                }
            }
            return new ResponseEntity<>(articles, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving articles item", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> createArticle(@RequestBody @Valid ArticleRequestTo articlesRequestTo) {
        try {
            ArticleResponseTo createdArticles = articlesService.save(articlesRequestTo);
            redisTemplate.delete("allArticles");
            return new ResponseEntity<>(createdArticles, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating articles", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateArticle(@RequestBody @Valid ArticleRequestTo articlesRequestTo) {
        try {
            ArticleResponseTo updatedArticles = articlesService.update(articlesRequestTo);
            if (updatedArticles != null) {
                redisTemplate.delete("allArticles");
                redisTemplate.delete("articles:" + updatedArticles.getId());
                return new ResponseEntity<>(updatedArticles, HttpStatus.OK);
            }
            return new ResponseEntity<>("Articles not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Error updating articles", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteArticles(@PathVariable Long id) {
        try {
            articlesService.deleteById(id);
            redisTemplate.delete("allArticles");
            redisTemplate.delete("articles:" + id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting articles", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}