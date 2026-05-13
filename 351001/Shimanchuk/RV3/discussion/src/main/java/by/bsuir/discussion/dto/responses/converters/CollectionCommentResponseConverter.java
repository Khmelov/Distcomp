package by.bsuir.discussion.dto.responses.converters;

import by.bsuir.discussion.domain.Comment;
import by.bsuir.discussion.dto.responses.CommentResponseDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = CommentResponseConverter.class)
public interface CollectionCommentResponseConverter {
    List<CommentResponseDto> toListDto(List<Comment> comments);
}
