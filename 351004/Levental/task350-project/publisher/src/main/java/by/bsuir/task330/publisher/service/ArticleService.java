package by.bsuir.task330.publisher.service;

import by.bsuir.task330.publisher.domain.Article;
import by.bsuir.task330.publisher.dto.request.ArticleRequestTo;
import by.bsuir.task330.publisher.dto.response.ArticleResponseTo;

import java.util.List;

public interface ArticleService {
    ArticleResponseTo create(ArticleRequestTo request);
    ArticleResponseTo update(ArticleRequestTo request);
    ArticleResponseTo findById(Long id);
    List<ArticleResponseTo> findAll(Integer page, Integer size, String sort, String filter, Long creatorId);
    void delete(Long id);

    Article requireEntity(Long id);
}
