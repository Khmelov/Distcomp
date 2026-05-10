package com.example.publisher.controller;

import com.example.publisher.dto.request.ArticleRequestTo;
import com.example.publisher.dto.response.ArticleResponseTo;
import com.example.publisher.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v2.0/articles")
@RequiredArgsConstructor
public class ArticleControllerV2 {
    private final ArticleService service;

    // Создавать могут все (но по-хорошему нужно проверять, что authorId в request совпадает с текущим пользователем)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or @securityEvaluator.isSelf(#request.authorId, authentication.name)")
    public ArticleResponseTo create(@Valid @RequestBody ArticleRequestTo request) {
        return service.create(request);
    }

    @GetMapping
    public List<ArticleResponseTo> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public ArticleResponseTo getById(@PathVariable Long id) { return service.getById(id); }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityEvaluator.isArticleOwner(#id, authentication.name)")
    public ArticleResponseTo update(@PathVariable Long id, @Valid @RequestBody ArticleRequestTo request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN') or @securityEvaluator.isArticleOwner(#id, authentication.name)")
    public void delete(@PathVariable Long id) { service.delete(id); }
}