package com.example.lab1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.example.lab1.dto.PostRequestTo;
import com.example.lab1.dto.PostResponseTo;
import com.example.lab1.model.Post;

@Mapper
public interface PostMapper {

    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(target = "id", ignore = true)
    Post toEntity(PostRequestTo dto);

    PostResponseTo toDto(Post entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "newsId", source = "dto.newsId"),
            @Mapping(target = "content", source = "dto.content"),
    })
    Post updateEntity(PostRequestTo dto, Post existing);
}
