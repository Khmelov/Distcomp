package com.task.rest.util;

import com.task.rest.dto.CommentRequestTo;
import com.task.rest.dto.CommentResponseTo;
import com.task.rest.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    Comment toEntity(CommentRequestTo requestTo);
    CommentResponseTo toResponse(Comment comment);
    void updateEntityFromDto(CommentRequestTo requestTo, @MappingTarget Comment comment);
}
