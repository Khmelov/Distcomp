package com.github.Lexya06.startrestapp.publisher.impl.service.mapper.realization;

import com.github.Lexya06.startrestapp.publisher.api.dto.article.ArticleRequestTo;
import com.github.Lexya06.startrestapp.publisher.api.dto.article.ArticleResponseTo;
import com.github.Lexya06.startrestapp.publisher.impl.model.entity.realization.Article;
import com.github.Lexya06.startrestapp.publisher.impl.service.mapper.config.CentralMapperConfig;
import com.github.Lexya06.startrestapp.publisher.impl.service.mapper.impl.GenericMapperImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", config = CentralMapperConfig.class, uses = LabelMapper.class)
public interface ArticleMapper extends GenericMapperImpl<Article, ArticleRequestTo, ArticleResponseTo> {
    @Override
    @Mapping(source = "user.id", target = "userId")
    ArticleResponseTo createResponseFromEntity(Article article);
}