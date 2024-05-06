package by.bsuir.dc.lab4.dto.mappers;
import by.bsuir.dc.lab4.dto.*;
import by.bsuir.dc.lab4.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentMapper instance = Mappers.getMapper(CommentMapper.class);
    Comment convertFromDTO(CommentRequestTo dto);
    CommentRequestTo convertRequestToDTO(CommentRequestTo dto);
    CommentResponseTo convertResponseToDTO(Comment comment);

    CommentRequestTo convertCommentToRequest(Comment comment);

    Comment convertRequestToComment(CommentRequestTo dto);
}
