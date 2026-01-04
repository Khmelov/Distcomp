package com.example.entitiesapp.repositories;

import com.example.entitiesapp.entities.Article;
import java.util.List;

public interface ArticleRepository extends CrudRepository<Article, Long> {
    List<Article> findByWriterId(Long writerId);
    List<Article> findByStickerId(Long stickerId);
    List<Article> findByStickerName(String stickerName);
}