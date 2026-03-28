package by.distcomp.app.controller;

import by.distcomp.app.dto.ArticleRequestTo;
import by.distcomp.app.dto.ArticleResponseTo;
import by.distcomp.app.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/articles")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/{article-id}")
    public ArticleResponseTo getArticle(@PathVariable ("article-id") Long articleId){
        return  articleService.getArticleById(articleId);
    }

    @GetMapping
    public List<ArticleResponseTo> getArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return articleService.getArticlesPage(pageable);
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