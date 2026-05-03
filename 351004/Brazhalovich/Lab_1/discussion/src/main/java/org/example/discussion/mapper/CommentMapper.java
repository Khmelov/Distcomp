package org.example.discussion.mapper;

import org.example.discussion.dto.request.CommentRequestTo;
import org.example.discussion.dto.response.CommentResponseTo;
import org.example.discussion.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    Comment toEntity(CommentRequestTo request);

    CommentResponseTo toDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(CommentRequestTo request, @MappingTarget Comment comment);
}