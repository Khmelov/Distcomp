package org.example.mapper;

import org.example.dto.PostResponseTo;
import org.example.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "newsId", source = "newsId")
    PostResponseTo toResponse(Post post);
}