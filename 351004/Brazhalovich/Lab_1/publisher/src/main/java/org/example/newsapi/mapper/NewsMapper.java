package org.example.newsapi.mapper;

import org.example.newsapi.dto.request.NewsRequestTo;
import org.example.newsapi.dto.response.NewsResponseTo;
import org.example.newsapi.entity.Marker;
import org.example.newsapi.entity.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", imports = {Collectors.class, Set.class, Marker.class})
public interface NewsMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "markers", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    News toEntity(NewsRequestTo request);

    @Mapping(target = "marker", ignore = true)
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "markerIds", expression = "java(news.getMarkers() != null ? news.getMarkers().stream().map(Marker::getId).collect(Collectors.toSet()) : new java.util.HashSet<>())")
    NewsResponseTo toDto(News news);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "markers", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    void updateEntityFromDto(NewsRequestTo request, @MappingTarget News news);
}