package com.github.Lexya06.startrestapp.model.repository.realization;

import com.github.Lexya06.startrestapp.model.entity.realization.Article;
import com.github.Lexya06.startrestapp.model.repository.impl.MyCrudRepositoryImpl;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends MyCrudRepositoryImpl<Article> {

}
