package com.github.Lexya06.startrestapp.publisher.impl.controller.realization.v2;

import com.github.Lexya06.startrestapp.publisher.api.dto.article.ArticleRequestTo;
import com.github.Lexya06.startrestapp.publisher.api.dto.article.ArticleResponseTo;
import com.github.Lexya06.startrestapp.publisher.impl.controller.abstraction.BaseController;
import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.Article;
import com.github.Lexya06.startrestapp.publisher.impl.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.publisher.impl.service.realization.ArticleService;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2.0/articles")
@Validated
public class ArticleControllerV2 extends BaseController<Article, ArticleRequestTo, ArticleResponseTo> {
    private final ArticleService articleService;

    @Autowired
    public ArticleControllerV2(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    protected BaseEntityService<Article, ArticleRequestTo, ArticleResponseTo> getBaseService() {
        return articleService;
    }

    @Override
    public ResponseEntity<List<ArticleResponseTo>> getAllEntities(@QuerydslPredicate(root = Article.class) Predicate predicate, Pageable pageable) {
        return getAllEntitiesBase(predicate, pageable);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @articleService.getEntityById(#id).userId == principal.id)")
    public ResponseEntity<ArticleResponseTo> updateEntity(Long id, ArticleRequestTo requestDTO) {
        return super.updateEntity(id, requestDTO);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or (hasRole('CUSTOMER') and @articleService.getEntityById(#id).userId == principal.id)")
    public ResponseEntity<Void> deleteEntity(Long id) {
        return super.deleteEntity(id);
    }
}
