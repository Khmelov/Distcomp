package by.bsuir.publisher.dto.requests.converters;

import by.bsuir.publisher.domain.Comment;
import by.bsuir.publisher.dto.requests.CommentRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentRequestConverter {
    Comment fromDto(CommentRequestDto comment);
}
