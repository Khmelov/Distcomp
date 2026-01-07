package com.task310.discussion.mapper;

import com.task310.discussion.dto.PostRequestTo;
import com.task310.discussion.dto.PostResponseTo;
import com.task310.discussion.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "key", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Post toEntity(PostRequestTo dto);

    @Mapping(target = "id", expression = "java(entity.getId())")
    @Mapping(target = "articleId", expression = "java(entity.getArticleId())")
    @Mapping(target = "state", expression = "java(entity.getState())")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "created", source = "created")
    @Mapping(target = "modified", source = "modified")
    PostResponseTo toResponseDto(Post entity);

    List<PostResponseTo> toResponseDtoList(List<Post> entities);

    @Mapping(target = "key", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    void updateEntityFromDto(PostRequestTo dto, @MappingTarget Post entity);
}
