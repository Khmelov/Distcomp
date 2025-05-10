package com.example.discussion.service.mapper;

import com.example.discussion.dto.PostRequestTo;
import com.example.discussion.dto.PostResponseTo;
import com.example.discussion.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {
    Post toEntity(PostRequestTo dto); // Преобразует DTO в сущность

    PostResponseTo toDto(Post entity); // Преобразует сущность в DTO

    void updateEntityFromDto(PostRequestTo dto, @MappingTarget Post entity);
}
