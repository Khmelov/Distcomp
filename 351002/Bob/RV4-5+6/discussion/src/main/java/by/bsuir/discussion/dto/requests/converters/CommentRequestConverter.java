package by.bsuir.discussion.dto.requests.converters;

import by.bsuir.discussion.domain.Comment;
import by.bsuir.discussion.dto.requests.CommentRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentRequestConverter {
    @Mapping(target = "state", ignore = true)
    Comment fromDto(CommentRequestDto comment);
}
