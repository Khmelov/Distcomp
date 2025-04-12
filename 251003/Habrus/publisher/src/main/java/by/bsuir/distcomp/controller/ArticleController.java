package by.bsuir.distcomp.controller;

import by.bsuir.distcomp.dto.request.ArticleRequestTo;
import by.bsuir.distcomp.dto.response.ArticleResponseTo;
import by.bsuir.distcomp.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1.0/articles")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    ResponseEntity<List<ArticleResponseTo>> getAllArticles() {
        return new ResponseEntity<>(articleService.getAllArticles(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id:\\d+}")
    ResponseEntity<ArticleResponseTo> getArticleById(@PathVariable Long id) {
        return new ResponseEntity<>(articleService.getArticleById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ArticleResponseTo> createArticle(@RequestBody @Valid ArticleRequestTo articleRequestTo) {
        return new ResponseEntity<>(articleService.createArticle(articleRequestTo), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<ArticleResponseTo> updateArticle(@RequestBody @Valid ArticleRequestTo articleRequestTo) {
        return new ResponseEntity<>(articleService.updateArticle(articleRequestTo), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id:\\d+}")
    public ResponseEntity<ArticleResponseTo> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
