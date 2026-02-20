package com.example.lab.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.lab.dto.NewsRequestTo;
import com.example.lab.dto.NewsResponseTo;
import com.example.lab.dto.UserResponseTo;
import com.example.lab.service.NewsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1.0/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<List<NewsResponseTo>> getAllNews() {
        return ResponseEntity.ok(newsService.getAllNews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponseTo> getNews(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getNewsById(id));
    }

    @PostMapping
    public ResponseEntity<NewsResponseTo> createNews(@Valid @RequestBody NewsRequestTo news) {
        return ResponseEntity.status(HttpStatus.CREATED).body(newsService.createNews(news));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NewsResponseTo> updateNews(@PathVariable Long id, @Valid @RequestBody NewsRequestTo news) {
        return ResponseEntity.ok(newsService.updateNews(id, news));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponseTo> getUserByNewsId(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.getUserByNewsId(id));
    }
}