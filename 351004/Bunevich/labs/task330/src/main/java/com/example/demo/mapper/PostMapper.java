package com.example.demo.mapper;

import com.example.demo.dto.request.PostRequestTo;
import com.example.demo.dto.response.PostResponseTo;
import com.example.demo.models.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "story", ignore = true)
    Post toEntity(PostRequestTo request);

    @Mapping(target = "storyId", source = "story.id")
    PostResponseTo toResponse(Post post);

    List<PostResponseTo> toEntityList(List<Post> postList);
}
