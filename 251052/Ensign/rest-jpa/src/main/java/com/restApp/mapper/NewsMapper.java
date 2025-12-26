package com.restApp.mapper;

import com.restApp.dto.NewsRequestTo;
import com.restApp.dto.NewsResponseTo;
import com.restApp.model.News;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { CommentMapper.class, MarkMapper.class })
public interface NewsMapper {

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "marks", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    News toEntity(NewsRequestTo request);

    @Mapping(source = "author.id", target = "authorId")
    NewsResponseTo toResponse(News entity);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "marks", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    void updateEntity(@MappingTarget News entity, NewsRequestTo request);
}
