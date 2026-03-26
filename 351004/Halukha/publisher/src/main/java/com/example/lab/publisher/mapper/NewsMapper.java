package com.example.lab.publisher.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.example.lab.publisher.dto.NewsRequestTo;
import com.example.lab.publisher.dto.NewsResponseTo;
import com.example.lab.publisher.model.News;

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
            @Mapping(target = "created", source = "dto.created"),
            @Mapping(target = "modified", source = "dto.modified"),
            @Mapping(target = "markers", ignore = true),
    })
    News updateEntity(NewsRequestTo dto, News existing);
}
