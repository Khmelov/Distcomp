package by.bsuir.discussion.dto.requests.converters;

import by.bsuir.discussion.domain.Comment;
import by.bsuir.discussion.dto.requests.CommentRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentRequestConverter {
    Comment fromDto(CommentRequestDto comment);
}
