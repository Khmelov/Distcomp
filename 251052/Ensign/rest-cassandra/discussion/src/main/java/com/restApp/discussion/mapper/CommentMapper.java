package com.restApp.discussion.mapper;

import com.restApp.discussion.dto.CommentRequestTo;
import com.restApp.discussion.dto.CommentResponseTo;
import com.restApp.discussion.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment toEntity(CommentRequestTo request);

    CommentResponseTo toResponse(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "country", ignore = true) // PKs shouldn't usually change
    void updateEntity(@MappingTarget Comment comment, CommentRequestTo request);
}
