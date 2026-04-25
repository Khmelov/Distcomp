package com.example.demo.mapper;

import com.example.demo.dto.request.PostRequestTo;
import com.example.demo.dto.response.PostResponseTo;
import com.example.demo.models.Post;
import com.example.demo.models.Tag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {
    Post toEntity(PostRequestTo request);
    PostResponseTo toResponse(Post post);
    List<PostResponseTo> toEntityList(List<Post> tagList);
}
