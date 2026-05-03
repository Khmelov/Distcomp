package com.example.task310.mapper;

import com.example.task310.dto.PostRequestTo;
import com.example.task310.dto.PostResponseTo;
import com.example.task310.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface EntityMapper {

    Post toEntity(PostRequestTo dto);

    PostResponseTo toResponse(Post entity);

    List<PostResponseTo> toPostResponseList(List<Post> entityList);
}