package com.bsuir.romanmuhtasarov.domain.mapper;

import com.bsuir.romanmuhtasarov.domain.request.MessageRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.MessageResponseTo;
import org.mapstruct.Mapper;
import com.bsuir.romanmuhtasarov.domain.entity.Message;

import java.util.List;

@Mapper(componentModel = "spring", uses = MessageMapper.class)
public interface MessageListMapper {
    List<Message> toMessageList(List<MessageRequestTo> messageRequestToList);

    List<MessageResponseTo> toMessageResponseToList(List<Message> messageList);
}
