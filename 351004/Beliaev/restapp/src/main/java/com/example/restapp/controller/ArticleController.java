package com.example.discussion.controller;

import com.example.discussion.dto.request.ArticleRequestTo;
import com.example.discussion.dto.response.ArticleResponseTo;
import com.example.discussion.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleResponseTo create(@Valid @RequestBody ArticleRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<ArticleResponseTo> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ArticleResponseTo getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public ArticleResponseTo update(@PathVariable Long id, @Valid @RequestBody ArticleRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}