package by.distcomp.task1.controller;

import by.distcomp.task1.dto.ArticleRequestTo;
import by.distcomp.task1.dto.ArticleResponseTo;
import by.distcomp.task1.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/articles")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }
    @GetMapping
    public List<ArticleResponseTo> getAllArticles(){
        return  articleService.getAllArticles();
    }
    @GetMapping("/{article-id}")
    public ArticleResponseTo getArticle(@PathVariable ("article-id") Long articleId){
        return  articleService.getArticleById(articleId);
    }
    @PostMapping
    public ResponseEntity<ArticleResponseTo> createArticle(@Valid @RequestBody ArticleRequestTo request){
       ArticleResponseTo createdArticle = articleService.createArticle(request);
        URI location = ServletUriComponentsBuilder
               .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdArticle.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(createdArticle);
    }
    @PutMapping("/{article-id}")
    public ResponseEntity<ArticleResponseTo> updateArticle(@PathVariable ("article-id") Long articleId, @Valid @RequestBody ArticleRequestTo request){
        ArticleResponseTo user =  articleService.updateArticle(articleId, request);
        return ResponseEntity.ok(user);
    }
    @DeleteMapping("/{article-id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable ("article-id") Long articleId){
        articleService.deleteArticle(articleId);
        return ResponseEntity.noContent().build();
    }
}