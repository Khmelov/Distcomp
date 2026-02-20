package com.github.Lexya06.startrestapp.service.realization;

import com.github.Lexya06.startrestapp.model.dto.request.ArticleRequestTo;
import com.github.Lexya06.startrestapp.model.dto.response.ArticleResponseTo;
import com.github.Lexya06.startrestapp.model.entity.realization.Article;
import com.github.Lexya06.startrestapp.model.entity.realization.User;
import com.github.Lexya06.startrestapp.model.repository.abstraction.MyCrudRepository;
import com.github.Lexya06.startrestapp.model.repository.realization.ArticleRepository;
import com.github.Lexya06.startrestapp.model.repository.realization.UserRepository;
import com.github.Lexya06.startrestapp.service.abstraction.BaseEntityService;
import com.github.Lexya06.startrestapp.service.customexception.MyEntityNotFoundException;
import com.github.Lexya06.startrestapp.service.mapper.impl.GenericMapperImpl;
import com.github.Lexya06.startrestapp.service.mapper.realization.ArticleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleService extends BaseEntityService<Article, ArticleRequestTo, ArticleResponseTo> {
    ArticleRepository articleRepository;
    UserRepository userRepository;
    ArticleMapper articleMapper;

    @Autowired
    public ArticleService(ArticleRepository articleRepository, UserRepository userRepository, ArticleMapper articleMapper) {
        super(Article.class);
        this.articleRepository = articleRepository;
        this.articleMapper = articleMapper;
        this.userRepository = userRepository;
    }

    @Override
    protected MyCrudRepository<Article> getRepository() {
        return articleRepository;
    }

    @Override
    protected GenericMapperImpl<Article, ArticleRequestTo, ArticleResponseTo> getMapper() {
        return articleMapper;
    }

    @Override
    public ArticleResponseTo createEntity(ArticleRequestTo request) {
        User u = userRepository.findById(request.getUserId()).orElseThrow(()->new MyEntityNotFoundException(request.getUserId(), User.class));
        Article article = articleMapper.createEntityFromRequest(request);
        article.setUser(u);
        article = articleRepository.save(article);
        return articleMapper.createResponseFromEntity(article);
    }
}
