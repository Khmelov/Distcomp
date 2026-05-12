package com.example.discussion.mapper;

import com.example.common.dto.MessageRequestTo;
import com.example.common.dto.MessageResponseTo;
import com.example.discussion.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(source = "key.id", target = "id")
    @Mapping(source = "key.articleId", target = "articleId")
    MessageResponseTo toResponse(Message message);

    @Mapping(target = "key.id", ignore = true)
    @Mapping(target = "key.articleId", source = "articleId")
    @Mapping(target = "content", source = "content", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "country", ignore = true)
    Message toEntity(MessageRequestTo request);


    List<MessageResponseTo> toResponseList(List<Message> messages);
}