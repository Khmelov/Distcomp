package org.polozkov.mapper.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.polozkov.dto.comment.CommentRequestTo;
import org.polozkov.dto.comment.CommentResponseTo;
import org.polozkov.entity.comment.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentResponseTo commentToResponseDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    Comment requestDtoToComment(CommentRequestTo commentRequest);
}
