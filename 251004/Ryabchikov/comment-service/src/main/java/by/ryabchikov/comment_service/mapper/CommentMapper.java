package by.ryabchikov.comment_service.mapper;

import by.ryabchikov.comment_service.dto.CommentRequestTo;
import by.ryabchikov.comment_service.dto.CommentResponseTo;
import by.ryabchikov.comment_service.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment toComment(CommentRequestTo commentRequestTo);

    CommentResponseTo toCommentResponseTo(Comment comment);
}
