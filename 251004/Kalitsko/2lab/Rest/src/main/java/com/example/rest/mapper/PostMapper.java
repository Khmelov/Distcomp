package com.example.rest.mapper;

import com.example.rest.dto.post.PostRequestTo;
import com.example.rest.dto.post.PostResponseTo;
import com.example.rest.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "topic.id", source = "topicId")
    Post toEntity(PostRequestTo request);
    @Mapping(target = "topicId", source = "topic.id")
    PostResponseTo toResponse(Post post);
}