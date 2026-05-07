package com.github.Lexya06.startrestapp.publisher.impl.service.realization;

import com.github.Lexya06.startrestapp.publisher.api.dto.article.ArticleRequestTo;
import com.github.Lexya06.startrestapp.publisher.api.dto.article.ArticleResponseTo;
import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.Article;
import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.Label;
import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.QArticle;
import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.User;
import com.github.Lexya06.startrestapp.publisher.impl.model.repository.impl.MyCrudRepositoryImpl;
import com.github.Lexya06.startrestapp.publisher.impl.model.repository.realization.ArticleRepository;
import com.github.Lexya06.startrestapp.publisher.impl.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.publisher.impl.service.customexception.MyEntityNotFoundException;
import com.github.Lexya06.startrestapp.publisher.impl.service.mapper.impl.GenericMapperImpl;
import com.github.Lexya06.startrestapp.publisher.impl.service.mapper.realization.ArticleMapper;
import com.querydsl.core.types.Predicate;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@CacheConfig(cacheNames = "articles")
public class ArticleService extends BaseEntityService<Article, ArticleRequestTo, ArticleResponseTo> {
    final ArticleRepository articleRepository;
    final UserService userService;
    final LabelService labelService;
    final ArticleMapper articleMapper;

    @Autowired
    public ArticleService(ArticleRepository articleRepository, UserService userService, LabelService labelService, ArticleMapper articleMapper) {
        super(Article.class);
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
        this.userService = userService;
        this.labelService = labelService;
    }

    @Override
    protected MyCrudRepositoryImpl<Article> getRepository() {
        return articleRepository;
    }

    @Override
    protected GenericMapperImpl<Article, ArticleRequestTo, ArticleResponseTo> getMapper() {
        return articleMapper;
    }


    @Override
    protected void preCreate(Article article, ArticleRequestTo request) {
        User u = userService.getEntityReferenceWithCheckExistingId(request.getUserId());
        article.setUser(u);
        article.setLabels(labelService.saveUnexistingLabelsByName(request.getLabels()));
    }

    public Long countArticles(Label label){
        QArticle qArticle = QArticle.article;
        Predicate predicate = qArticle.labels.contains(label);
        return articleRepository.count(predicate);
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public void deleteEntityById(Long id) {
        Article article = articleRepository.findById(id).orElseThrow(()-> new MyEntityNotFoundException(id, Article.class));
        Set<Label> labels = new HashSet<>(article.getLabels());
        for (Label label : labels) {
            if (this.countArticles(label) == 1){
                article.getLabels().remove(label);
                labelService.deleteEntityById(label.getId());
            }
        }
        articleRepository.delete(article);
    }

    @Override
    @Cacheable(key = "#id")
    public ArticleResponseTo getEntityById(Long id) {
        return super.getEntityById(id);
    }

    @Override
    @CachePut(key = "#id")
    public ArticleResponseTo updateEntity(Long id, ArticleRequestTo requestDTO) {
        return super.updateEntity(id, requestDTO);
    }
}
