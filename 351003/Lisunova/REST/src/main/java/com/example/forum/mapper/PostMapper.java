package com.example.forum.mapper;

import com.example.forum.dto.request.PostRequestTo;
import com.example.forum.dto.response.PostResponseTo;
import com.example.forum.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "id", ignore = true)
    Post toEntity(PostRequestTo dto);

    PostResponseTo toResponse(Post entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(PostRequestTo dto, @MappingTarget Post entity);
}
