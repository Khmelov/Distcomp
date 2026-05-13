package by.liza.app.controller;

import by.liza.app.dto.request.ArticleRequestTo;
import by.liza.app.dto.response.ArticleResponseTo;
import by.liza.app.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1.0/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArticleResponseTo create(@Valid @RequestBody ArticleRequestTo requestTo) {
        return articleService.create(requestTo);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ArticleResponseTo getById(@PathVariable Long id) {
        return articleService.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ArticleResponseTo> getAll() {
        return articleService.getAll();
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ArticleResponseTo update(@Valid @RequestBody ArticleRequestTo requestTo) {
        return articleService.update(requestTo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        articleService.delete(id);
    }
}