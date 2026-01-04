package com.example.entitiesapp.mappers;

import com.example.entitiesapp.dto.request.ArticleRequestTo;
import com.example.entitiesapp.dto.response.ArticleResponseTo;
import com.example.entitiesapp.entities.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "stickers", ignore = true)
    Article toEntity(ArticleRequestTo dto);

    ArticleResponseTo toResponseDto(Article entity);
}