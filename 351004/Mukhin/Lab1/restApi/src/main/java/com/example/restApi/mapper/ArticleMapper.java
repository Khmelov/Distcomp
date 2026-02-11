package com.example.restApi.mapper;

import com.example.restApi.dto.request.ArticleRequestTo;
import com.example.restApi.dto.response.ArticleResponseTo;
import com.example.restApi.model.Article;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class ArticleMapper {

    public Article toEntity(ArticleRequestTo request) {
        Article article = new Article();
        article.setUserId(request.getUserId());
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        if (request.getMarkerIds() != null) {
            article.setMarkerIds(new HashSet<>(request.getMarkerIds()));
        }
        return article;
    }

    public ArticleResponseTo toDto(Article article) {
        ArticleResponseTo response = new ArticleResponseTo();
        response.setId(article.getId());
        response.setUserId(article.getUserId());
        response.setTitle(article.getTitle());
        response.setContent(article.getContent());
        response.setCreated(article.getCreated());
        response.setModified(article.getModified());
        response.setMarkerIds(article.getMarkerIds());
        return response;
    }

    public void updateEntity(Article article, ArticleRequestTo request) {
        article.setUserId(request.getUserId());
        article.setTitle(request.getTitle());
        article.setContent(request.getContent());
        if (request.getMarkerIds() != null) {
            article.setMarkerIds(new HashSet<>(request.getMarkerIds()));
        }
    }
}
