package com.example.restApi.mapper;

import com.example.restApi.dto.request.CommentRequestTo;
import com.example.restApi.dto.response.CommentResponseTo;
import com.example.restApi.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment toEntity(CommentRequestTo request);
    CommentResponseTo toDto(Comment comment);
    void updateEntity(@MappingTarget Comment comment, CommentRequestTo request);
}
