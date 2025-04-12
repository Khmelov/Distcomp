package by.molchan.Discussion.utils.mappers;


import by.molchan.Discussion.DTOs.Requests.CommentRequestDTO;
import by.molchan.Discussion.DTOs.Responses.CommentResponseDTO;
import by.molchan.Discussion.models.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CommentsMapper {


    @Mapping(target = "id", expression = "java(comment.getKey().getId())")
    @Mapping(target = "articleId", expression = "java(comment.getKey().getArticleId())")
    CommentResponseDTO toCommentResponse(Comment comment);

    List<CommentResponseDTO> toCommentResponseList(Iterable<Comment> comments);
    @Mapping(target = "key", expression = "java(new Comment.CommentKey(commentRequestDTO.getArticleId()))")
    Comment toComment(CommentRequestDTO commentRequestDTO);
}
