package com.example.demo.controller;

import com.example.demo.dto.NewsRequest;
import com.example.demo.entity.News;
import com.example.demo.service.NewsService;
import com.example.demo.specification.NewsSpecification;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/news")
@Profile("docker")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @PostMapping
    public ResponseEntity<News> create(@Valid @RequestBody NewsRequest request) {
        News news = newsService.createFromRequest(request);
        return new ResponseEntity<>(news, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<News> update(@PathVariable Long id, @Valid @RequestBody NewsRequest request) {
        News news = newsService.updateFromRequest(id, request);
        return ResponseEntity.ok(news);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        newsService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<News> findById(@PathVariable Long id) {
        return newsService.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Плоский список (без пагинации) – для тестов
    @GetMapping
    public ResponseEntity<List<News>> findAllList(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Long authorId,
            @RequestParam(defaultValue = "id,asc") String[] sort) {

        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Sort sorting = Sort.by(direction, sort[0]);

        Specification<News> spec = Specification.where(NewsSpecification.titleContains(title))
                .and(NewsSpecification.contentContains(content))
                .and(NewsSpecification.authorIdEquals(authorId));

        List<News> newsList = newsService.findAll(spec, sorting);
        return ResponseEntity.ok(newsList);
    }

    // Пагинация – отдельный endpoint
    @GetMapping("/page")
    public ResponseEntity<Page<News>> findAllPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String[] sort,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) Long authorId) {

        Sort.Direction direction = Sort.Direction.fromString(sort[1]);
        Sort sorting = Sort.by(direction, sort[0]);
        Pageable pageable = PageRequest.of(page, size, sorting);

        Specification<News> spec = Specification.where(NewsSpecification.titleContains(title))
                .and(NewsSpecification.contentContains(content))
                .and(NewsSpecification.authorIdEquals(authorId));

        return ResponseEntity.ok(newsService.findAll(spec, pageable));
    }
}