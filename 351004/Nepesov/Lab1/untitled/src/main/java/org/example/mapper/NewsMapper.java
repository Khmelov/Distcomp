package org.example.mapper;

import org.example.dto.NewsRequestTo;
import org.example.dto.NewsResponseTo;
import org.example.model.News;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NewsMapper {
    News toEntity(NewsRequestTo requestTo);
    NewsResponseTo toResponse(News entity);
}