package by.rest.publisher.mapper;

import by.rest.publisher.domain.Comment;
import by.rest.publisher.dto.comment.CommentRequestTo;
import by.rest.publisher.dto.comment.CommentResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    
    @Mapping(target = "id", expression = "java(generateId())")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "author", source = "author")
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "modifiedDate", expression = "java(java.time.Instant.now())")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "storyId", target = "storyId")
    Comment toEntity(CommentRequestTo dto);
    
    @Mapping(source = "id", target = "id")
    @Mapping(source = "storyId", target = "storyId")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "author", target = "author")
    @Mapping(source = "status", target = "status")
    CommentResponseTo toResponse(Comment entity);
    
    default UUID generateId() {
        return UUID.randomUUID();
    }
    
    default Comment toEntity(CommentRequestTo dto, UUID id) {
        Comment comment = toEntity(dto);
        comment.setId(id);
        return comment;
    }
}