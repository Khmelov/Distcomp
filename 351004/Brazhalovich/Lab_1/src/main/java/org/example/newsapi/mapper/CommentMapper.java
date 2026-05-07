package org.example.newsapi.mapper;

import org.example.newsapi.dto.request.CommentRequestTo;
import org.example.newsapi.dto.response.CommentResponseTo;
import org.example.newsapi.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "news", ignore = true) // Заполняется в CommentService
    Comment toEntity(CommentRequestTo request);

    @Mapping(target = "newsId", source = "news.id")
    CommentResponseTo toDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "news", ignore = true)
    void updateEntityFromDto(CommentRequestTo request, @MappingTarget Comment comment);
}