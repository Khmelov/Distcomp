package com.github.Lexya06.startrestapp.service.mapper.realization;

import com.github.Lexya06.startrestapp.model.dto.request.ArticleRequestTo;
import com.github.Lexya06.startrestapp.model.dto.response.ArticleResponseTo;
import com.github.Lexya06.startrestapp.model.entity.realization.Article;
import com.github.Lexya06.startrestapp.service.mapper.config.CentralMapperConfig;
import com.github.Lexya06.startrestapp.service.mapper.impl.GenericMapperImpl;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring", config = CentralMapperConfig.class)
public interface ArticleMapper extends GenericMapperImpl<Article, ArticleRequestTo, ArticleResponseTo> {
    @Override
    @Mapping(source = "user.id", target = "userId")
    ArticleResponseTo createResponseFromEntity(Article article);
}