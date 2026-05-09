package com.example.Task310.mapper;

import com.example.Task310.bean.Post;
import com.example.Task310.dto.PostRequestTo;
import com.example.Task310.dto.PostResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(source = "story.id", target = "storyId")
    PostResponseTo toResponse(Post post);

    @Mapping(target = "story", ignore = true)
    Post toEntity(PostRequestTo request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "story", ignore = true)
    void updateEntity(PostRequestTo request, @MappingTarget Post post);
}