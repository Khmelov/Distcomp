package com.example.lab.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.example.lab.dto.NewsRequestTo;
import com.example.lab.dto.NewsResponseTo;
import com.example.lab.model.News;

@Mapper
public interface NewsMapper {

    NewsMapper INSTANCE = Mappers.getMapper(NewsMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "markers", ignore = true)
    News toEntity(NewsRequestTo dto);

    NewsResponseTo toDto(News entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "userId", source = "dto.userId"),
            @Mapping(target = "title", source = "dto.title"),
            @Mapping(target = "content", source = "dto.content"),
            @Mapping(target = "markers", ignore = true),
            @Mapping(target = "created", source = "dto.created"),
            @Mapping(target = "modified", source = "dto.modified"),
    })
    News updateEntity(NewsRequestTo dto, News existing);
}
