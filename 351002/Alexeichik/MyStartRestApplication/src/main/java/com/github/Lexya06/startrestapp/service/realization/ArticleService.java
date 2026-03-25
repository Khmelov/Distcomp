package com.github.Lexya06.startrestapp.service.realization;

import com.github.Lexya06.startrestapp.model.dto.request.ArticleRequestTo;
import com.github.Lexya06.startrestapp.model.dto.request.LabelRequestTo;
import com.github.Lexya06.startrestapp.model.dto.response.ArticleResponseTo;
import com.github.Lexya06.startrestapp.model.entity.realization.Article;
import com.github.Lexya06.startrestapp.model.entity.realization.Label;
import com.github.Lexya06.startrestapp.model.entity.realization.QArticle;
import com.github.Lexya06.startrestapp.model.entity.realization.User;
import com.github.Lexya06.startrestapp.model.repository.impl.MyCrudRepositoryImpl;
import com.github.Lexya06.startrestapp.model.repository.realization.ArticleRepository;
import com.github.Lexya06.startrestapp.model.repository.realization.LabelRepository;
import com.github.Lexya06.startrestapp.model.repository.realization.UserRepository;
import com.github.Lexya06.startrestapp.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.service.customexception.MyEntitiesNotFoundException;
import com.github.Lexya06.startrestapp.service.customexception.MyEntityNotFoundException;
import com.github.Lexya06.startrestapp.service.mapper.impl.GenericMapperImpl;
import com.github.Lexya06.startrestapp.service.mapper.realization.ArticleMapper;
import com.querydsl.core.types.Predicate;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
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
    @Transactional
    public ArticleResponseTo createEntity(ArticleRequestTo request) {

        User u = userService.getEntityReferenceWithCheckExistingId(request.getUserId());
        Article article = articleMapper.createEntityFromRequest(request);
        article.setUser(u);

        article.setLabels(labelService.saveUnexistingLabelsByName(request.getLabels()));

        article = articleRepository.save(article);
        return articleMapper.createResponseFromEntity(article);
    }

    public Long countArticles(Label label){
        QArticle qArticle = QArticle.article;
        Predicate predicate = qArticle.labels.contains(label);
        return articleRepository.count(predicate);
    }

    @Override
    @Transactional
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
}
