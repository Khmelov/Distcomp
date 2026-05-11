package by.bsuir.task330.publisher.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import by.bsuir.task330.publisher.service.ArticleService;
import by.bsuir.task330.publisher.dto.request.ArticleRequestTo;
import by.bsuir.task330.publisher.dto.response.ArticleResponseTo;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/{id}")
    public ArticleResponseTo getById(@PathVariable Long id) {
        return articleService.findById(id);
    }

    @GetMapping
    public List<ArticleResponseTo> getAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Long creatorId
    ) {
        return articleService.findAll(page, size, sort, filter, creatorId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleResponseTo create(@Valid @RequestBody ArticleRequestTo request) {
        return articleService.create(request);
    }

    @PutMapping
    public ArticleResponseTo update(@Valid @RequestBody ArticleRequestTo request) {
        return articleService.update(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        articleService.delete(id);
    }
}
