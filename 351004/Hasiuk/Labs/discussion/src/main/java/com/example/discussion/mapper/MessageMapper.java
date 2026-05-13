package com.example.discussion.mapper;

import com.example.discussion.dto.request.MessageRequestTo;
import com.example.discussion.dto.response.MessageResponseTo;
import com.example.discussion.entity.Message;
import com.example.discussion.entity.MessageKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "key", ignore = true)
    Message toEntity(MessageRequestTo dto);

    default Message toEntityWithId(MessageRequestTo dto, Long id) {
        Message message = toEntity(dto);
        MessageKey key = new MessageKey();
        key.setStoryId(dto.getStoryId());
        key.setId(id);
        message.setKey(key);
        return message;
    }

    @Mapping(source = "key.id", target = "id")
    @Mapping(source = "key.storyId", target = "storyId")
    MessageResponseTo toDto(Message entity);
}