package com.bsuir.romanmuhtasarov.domain.mapper;

import com.bsuir.romanmuhtasarov.domain.request.CommentRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.CommentResponseTo;
import org.mapstruct.Mapper;
import com.bsuir.romanmuhtasarov.domain.entity.Comment;

import java.util.List;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface CommentListMapper {
    List<Comment> toCommentList(List<CommentRequestTo> commentRequestToList);

    List<CommentResponseTo> toCommentResponseToList(List<Comment> commentList);
}
