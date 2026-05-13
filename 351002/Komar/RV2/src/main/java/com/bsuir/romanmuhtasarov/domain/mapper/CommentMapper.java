package com.bsuir.romanmuhtasarov.domain.mapper;

import com.bsuir.romanmuhtasarov.domain.entity.Comment;
import com.bsuir.romanmuhtasarov.domain.request.CommentRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.CommentResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = NewsMapper.class)
public interface CommentMapper {
    @Mapping(source = "newsId", target = "news.id")
    Comment toComment(CommentRequestTo commentRequestTo);
    @Mapping(source = "news.id", target = "newsId")
    CommentResponseTo toCommentResponseTo(Comment comment);
}
