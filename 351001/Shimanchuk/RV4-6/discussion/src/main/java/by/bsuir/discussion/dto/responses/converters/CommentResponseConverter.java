package by.bsuir.discussion.dto.responses.converters;

import by.bsuir.discussion.domain.Comment;
import by.bsuir.discussion.dto.responses.CommentResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentResponseConverter {
    CommentResponseDto toDto(Comment comment);
}
