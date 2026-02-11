package com.github.Lexya06.startrestapp.model.repository.realization;

import com.github.Lexya06.startrestapp.model.entity.realization.Article;
import com.github.Lexya06.startrestapp.model.repository.abstraction.MyCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ArticleRepository extends MyCrudRepository<Article> {
    Map<Long, Article> articleMap = new HashMap<>();
    @Override
    protected Map<Long, Article> getBaseEntityMap() {
        return articleMap;
    }
}
