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
    Post toEntity(PostRequestTo dto);

    PostResponseTo toResponseDto(Post entity);
}