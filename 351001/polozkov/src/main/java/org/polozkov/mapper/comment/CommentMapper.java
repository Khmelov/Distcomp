package org.polozkov.mapper.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.polozkov.dto.comment.CommentRequestTo;
import org.polozkov.dto.comment.CommentResponseTo;
import org.polozkov.entity.comment.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "issueId", source = "issue.id")
    CommentResponseTo commentToResponseDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    Comment requestDtoToComment(CommentRequestTo commentRequest);


    @Mapping(target = "id", ignore = true)
    Comment updateComment(@MappingTarget Comment comment, CommentRequestTo commentRequest);
}
