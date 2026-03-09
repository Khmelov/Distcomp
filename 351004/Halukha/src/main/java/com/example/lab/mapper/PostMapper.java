package com.example.lab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.example.lab.dto.PostRequestTo;
import com.example.lab.dto.PostResponseTo;
import com.example.lab.model.Post;

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
