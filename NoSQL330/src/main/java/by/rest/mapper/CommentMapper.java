package by.rest.mapper;

import by.rest.domain.Comment;
import by.rest.dto.CommentRequestTo;
import by.rest.dto.CommentResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "story", ignore = true)
    Comment toEntity(CommentRequestTo dto);
    
    @Mapping(source = "story.id", target = "storyId")
    CommentResponseTo toResponse(Comment entity);
}