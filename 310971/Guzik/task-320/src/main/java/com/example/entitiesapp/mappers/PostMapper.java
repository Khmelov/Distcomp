package com.example.entitiesapp.mappers;

import com.example.entitiesapp.dto.request.PostRequestTo;
import com.example.entitiesapp.dto.response.PostResponseTo;
import com.example.entitiesapp.entities.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "article", ignore = true)
    Post toEntity(PostRequestTo dto);

    @Mapping(source = "article.id", target = "articleId")
    PostResponseTo toResponseDto(Post entity);
}