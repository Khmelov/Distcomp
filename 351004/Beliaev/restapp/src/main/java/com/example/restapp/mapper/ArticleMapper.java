package com.example.restapp.mapper;

import com.example.restapp.dto.request.ArticleRequestTo;
import com.example.restapp.dto.response.ArticleResponseTo;
import com.example.restapp.model.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
    Article toEntity(ArticleRequestTo request);
    ArticleResponseTo toResponse(Article entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    void updateEntityFromDto(ArticleRequestTo request, @MappingTarget Article entity);
}