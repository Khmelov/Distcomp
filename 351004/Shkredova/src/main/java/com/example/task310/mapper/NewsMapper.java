package com.example.task310.mapper;

import com.example.task310.dto.NewsRequestTo;
import com.example.task310.dto.NewsResponseTo;
import com.example.task310.model.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NewsMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "notices", ignore = true)
    @Mapping(target = "marks", ignore = true)
    News toEntity(NewsRequestTo request);

    @Mapping(source = "creator.id", target = "creatorId")
    NewsResponseTo toResponse(News news);
}