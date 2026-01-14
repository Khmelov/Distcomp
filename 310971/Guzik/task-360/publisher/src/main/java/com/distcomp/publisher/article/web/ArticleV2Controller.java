package com.distcomp.publisher.article.web;

import com.distcomp.publisher.article.dto.ArticleRequest;
import com.distcomp.publisher.article.dto.ArticleResponse;
import com.distcomp.publisher.article.service.ArticleService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2.0/articles")
public class ArticleV2Controller {

    private final ArticleService service;

    public ArticleV2Controller(ArticleService service) {
        this.service = service;
    }

    @PreAuthorize("@access.isAdmin() or @access.isSelfWriterId(#request.writerId)")
    @PostMapping
    public ResponseEntity<ArticleResponse> create(@Valid @RequestBody ArticleRequest request) {
        return service.create(request)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleResponse> get(@PathVariable long id) {
        return service.get(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ArticleResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    @PreAuthorize("@access.canWriteArticle(#request.writerId, #id)")
    @PutMapping("/{id}")
    public ResponseEntity<ArticleResponse> update(@PathVariable long id, @Valid @RequestBody ArticleRequest request) {
        return service.update(id, request).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("@access.isAdmin() or @access.isSelfArticleId(#id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        boolean deleted = service.delete(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
