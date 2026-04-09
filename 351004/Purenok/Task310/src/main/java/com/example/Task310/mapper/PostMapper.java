package com.example.Task310.mapper;

import com.example.Task310.bean.Post;
import com.example.Task310.dto.PostRequestTo;
import com.example.Task310.dto.PostResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {
    Post toEntity(PostRequestTo dto);
    PostResponseTo toDto(Post entity);
    void updateEntityFromDto(PostRequestTo dto, @MappingTarget Post entity);
}