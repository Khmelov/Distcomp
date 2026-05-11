package by.bsuir.romamuhtasarov.api;

import by.bsuir.romamuhtasarov.impl.bean.Comment;
import by.bsuir.romamuhtasarov.impl.dto.CommentRequestTo;
import by.bsuir.romamuhtasarov.impl.dto.CommentResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
@Mapper
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper( CommentMapper.class );

    Comment CommentResponseToToComment(CommentResponseTo commentResponseTo);
    Comment CommentRequestToToComment(CommentRequestTo commentRequestTo);

    CommentResponseTo CommentToCommentResponseTo(Comment comment);

    CommentRequestTo CommentToCommentRequestTo(Comment comment);
}