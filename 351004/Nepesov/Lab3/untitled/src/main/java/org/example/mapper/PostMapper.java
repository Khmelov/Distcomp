package org.example.mapper;

import org.example.dto.PostRequestTo;
import org.example.dto.PostResponseTo;
import org.example.model.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    Post toEntity(PostRequestTo request);

    PostResponseTo toResponse(Post entity);
}