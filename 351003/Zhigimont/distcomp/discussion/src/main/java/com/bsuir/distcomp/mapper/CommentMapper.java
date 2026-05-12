package com.bsuir.distcomp.mapper;

import com.bsuir.distcomp.dto.CommentRequestTo;
import com.bsuir.distcomp.dto.CommentResponseTo;
import com.bsuir.distcomp.entity.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "key", ignore = true)
    Comment toEntity(CommentRequestTo dto);

    @Mapping(source = "key.topicId", target = "topicId")
    @Mapping(source = "key.id", target = "id")
    CommentResponseTo toDto(Comment entity);
}