package org.example.newsapi.mapper;

import org.example.newsapi.dto.request.NewsRequestTo;
import org.example.newsapi.dto.response.NewsResponseTo;
import org.example.newsapi.entity.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    News toEntity(NewsRequestTo request);

    NewsResponseTo toDto(News news);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    void updateEntityFromDto(NewsRequestTo request, @MappingTarget News news);
}