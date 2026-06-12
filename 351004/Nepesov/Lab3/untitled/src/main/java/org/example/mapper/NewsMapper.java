package org.example.mapper;

import org.example.dto.NewsRequestTo;
import org.example.dto.NewsResponseTo;
import org.example.model.News;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NewsMapper {

    News toEntity(NewsRequestTo request);

    NewsResponseTo toResponse(News entity);
}