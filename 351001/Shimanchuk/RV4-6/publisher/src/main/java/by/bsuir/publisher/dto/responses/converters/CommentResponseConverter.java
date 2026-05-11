package by.bsuir.publisher.dto.responses.converters;

import by.bsuir.publisher.domain.Comment;
import by.bsuir.publisher.dto.responses.CommentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentResponseConverter {
    @Mapping(source = "tweetId", target = "newsId")
    CommentResponseDto toDto(Comment comment);

    @Mapping(source = "newsId", target = "tweetId")
    Comment fromDto(CommentResponseDto commentResponseDto);
}
