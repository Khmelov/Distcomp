package com.example.discussion.mapper;

import com.example.discussion.dto.ArticleRequestTo;
import com.example.discussion.dto.ArticleResponseTo;
import com.example.discussion.model.Article;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
    @Mapping(target = "marks", ignore = true)
    Article toEntity(ArticleRequestTo dto);
    @Mapping(source = "creator.id", target = "creatorId")
    ArticleResponseTo toDto(Article entity);
    @Mapping(target = "marks", ignore = true)
    void updateEntityFromDto(ArticleRequestTo dto, @MappingTarget Article entity);
}
