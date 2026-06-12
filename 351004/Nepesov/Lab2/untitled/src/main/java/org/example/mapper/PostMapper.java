package org.example.mapper;

import org.example.dto.PostRequestTo;
import org.example.dto.PostResponseTo;
import org.example.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "news.id", source = "newsId")
    Post toEntity(PostRequestTo request);

    @Mapping(target = "newsId", source = "news.id")
    PostResponseTo toResponse(Post entity);
}