package com.example.news.mapper;

import com.example.common.dto.ArticleRequestTo;
import com.example.common.dto.ArticleResponseTo;
import com.example.news.entity.Article;
import com.example.news.entity.Marker;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ArticleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "writer", ignore = true)
    @Mapping(target = "markers", ignore = true)
    Article toEntity(ArticleRequestTo request);

    @Mapping(target = "writerId", source = "writer.id")
    @Mapping(target = "markerIds", source = "markers", qualifiedByName = "mapMarkersToIds")
    @Mapping(target = "messages", ignore = true)
    ArticleResponseTo toResponse(Article entity);

    @Named("mapMarkersToIds")
    default List<Long> mapMarkersToIds(List<Marker> markers) {
        if (markers == null) {
            return null;
        }
        return markers.stream()
                .map(Marker::getId)
                .collect(Collectors.toList());
    }
}