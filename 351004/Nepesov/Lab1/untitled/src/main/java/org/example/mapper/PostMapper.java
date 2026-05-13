package org.example.mapper;

import org.example.dto.PostRequestTo;
import org.example.dto.PostResponseTo;
import org.example.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PostMapper {
    Post toEntity(PostRequestTo requestTo);
    PostResponseTo toResponse(Post entity);
}