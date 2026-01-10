package com.blog.mapper;

import com.blog.dto.ArticleRequestTo;
import com.blog.dto.ArticleResponseTo;
import com.blog.entity.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ArticleMapper {

    Article requestToToEntity(ArticleRequestTo request);

    ArticleResponseTo entityToResponseTo(Article entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Article updateEntityFromRequest(ArticleRequestTo request, @MappingTarget Article entity);
}