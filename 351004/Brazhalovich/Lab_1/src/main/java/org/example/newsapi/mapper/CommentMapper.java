package org.example.newsapi.mapper;

import org.example.newsapi.dto.request.CommentRequestTo;
import org.example.newsapi.dto.request.MarkerRequestTo;
import org.example.newsapi.dto.response.CommentResponseTo;
import org.example.newsapi.dto.response.MarkerResponseTo;
import org.example.newsapi.entity.Comment;
import org.example.newsapi.entity.Marker;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toEntity(CommentRequestTo request);
    CommentResponseTo toDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(CommentRequestTo request, @MappingTarget Comment comment);
}