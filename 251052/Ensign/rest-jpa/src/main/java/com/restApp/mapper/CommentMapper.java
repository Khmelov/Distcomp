package com.restApp.mapper;

import com.restApp.dto.CommentRequestTo;
import com.restApp.dto.CommentResponseTo;
import com.restApp.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "news", ignore = true)
    @Mapping(target = "id", ignore = true)
    Comment toEntity(CommentRequestTo request);

    @Mapping(source = "news.id", target = "newsId")
    CommentResponseTo toResponse(Comment entity);

    @Mapping(target = "news", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntity(@MappingTarget Comment entity, CommentRequestTo request);
}
