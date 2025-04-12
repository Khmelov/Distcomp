package org.example.discussion.mapper;

import org.example.discussion.dto.requestDto.MessageRequestTo;
import org.example.discussion.dto.responseDto.MessageResponseTo;
import org.example.discussion.dto.updateDto.MessageUpdateTo;
import org.example.discussion.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface MessageMapper {
    MessageResponseTo ToMessageResponseTo(Message Message);
    @Mapping(target = "country", expression = "java(\"US\")")
    Message ToMessage(MessageRequestTo MessageRequestTo);
    @Mapping(target = "country", expression = "java(\"US\")")
    Message ToMessage(MessageUpdateTo MessageUpdateTo);
}
