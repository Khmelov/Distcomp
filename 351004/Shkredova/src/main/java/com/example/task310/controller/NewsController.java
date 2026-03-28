package com.example.task310.controller;

import com.example.task310.dto.NewsRequestTo;
import com.example.task310.dto.NewsResponseTo;
import com.example.task310.service.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @PostMapping
    public ResponseEntity<NewsResponseTo> create(@Valid @RequestBody NewsRequestTo request) {
        NewsResponseTo response = newsService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<NewsResponseTo>> findAll() {
        return ResponseEntity.ok(newsService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsResponseTo> findById(@PathVariable Long id) {
        return ResponseEntity.ok(newsService.findById(id));
    }

    @PutMapping("/{id}")  // ДОЛЖЕН БЫТЬ ЭТОТ МЕТОД
    public ResponseEntity<NewsResponseTo> update(@PathVariable Long id,
                                                 @Valid @RequestBody NewsRequestTo request) {
        NewsResponseTo news = newsService.update(id, request);
        return ResponseEntity.ok(news);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        newsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/marks/{markName}")
    public ResponseEntity<NewsResponseTo> addMarkToNews(
            @PathVariable Long id,
            @PathVariable String markName) {
        NewsResponseTo news = newsService.addMarkToNews(id, markName);
        return ResponseEntity.ok(news);
    }

    @DeleteMapping("/{id}/marks/{markName}")
    public ResponseEntity<Void> removeMarkFromNews(
            @PathVariable Long id,
            @PathVariable String markName) {
        newsService.removeMarkFromNews(id, markName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/marks")
    public ResponseEntity<Void> removeAllMarksFromNews(@PathVariable Long id) {
        newsService.removeAllMarksFromNews(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/marks/{markName}")
    public ResponseEntity<Void> deleteMarkCompletely(@PathVariable String markName) {
        newsService.deleteMarkCompletely(markName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/with-marks")
    public ResponseEntity<Void> deleteNewsWithMarks(@PathVariable Long id) {
        newsService.deleteNewsAndOrphanMarks(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/ensure-mark/{markName}")
    public ResponseEntity<NewsResponseTo> ensureMarkAndAdd(
            @PathVariable Long id,
            @PathVariable String markName) {
        // Этот метод гарантированно создаст метку, если её нет
        return ResponseEntity.ok(newsService.addMarkToNews(id, markName));
    }
}