package org.example.mapper;

import org.example.dto.NewsRequestTo;
import org.example.dto.NewsResponseTo;
import org.example.model.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    @Mapping(target = "stickers", ignore = true)
    News toEntity(NewsRequestTo request);

    NewsResponseTo toResponse(News entity);
}