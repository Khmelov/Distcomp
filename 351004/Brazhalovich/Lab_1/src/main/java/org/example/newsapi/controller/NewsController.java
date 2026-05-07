package org.example.newsapi.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.newsapi.dto.request.NewsRequestTo;
import org.example.newsapi.dto.response.NewsResponseTo;
import org.example.newsapi.service.NewsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewsResponseTo create(@RequestBody @Valid NewsRequestTo request) {
        System.out.println(">>> CREATE NEWS REQUEST: " + request);
        System.out.println(">>> markerNames: " + request.getMarkerNames());
        return newsService.create(request);
    }

    @GetMapping
    public List<NewsResponseTo> getAll(@PageableDefault(size = 50) Pageable pageable) {
        return newsService.findAll(pageable).getContent();
    }

    @GetMapping("/{id}")
    public NewsResponseTo getById(@PathVariable Long id) {
        return newsService.findById(id);
    }

    @PutMapping("/{id}")
    public NewsResponseTo update(@PathVariable Long id, @RequestBody @Valid NewsRequestTo request) {
        return newsService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        newsService.delete(id);
    }
}