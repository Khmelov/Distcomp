package by.bsuir.publisher.dto.responses.converters;

import by.bsuir.publisher.domain.Comment;
import by.bsuir.publisher.dto.responses.CommentResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = CommentResponseConverter.class)
public interface CollectionCommentResponseConverter {
    List<CommentResponseDto> toListDto(List<Comment> comments);
}
