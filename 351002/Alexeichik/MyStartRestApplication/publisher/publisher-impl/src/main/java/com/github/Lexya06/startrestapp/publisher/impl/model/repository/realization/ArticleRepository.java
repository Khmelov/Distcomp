package com.github.Lexya06.startrestapp.publisher.impl.model.repository.realization;

import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.Article;
import com.github.Lexya06.startrestapp.publisher.impl.model.repository.impl.MyCrudRepositoryImpl;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends MyCrudRepositoryImpl<Article> {

}
