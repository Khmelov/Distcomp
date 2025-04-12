package com.bsuir.romanmuhtasarov.domain.mapper;

import com.bsuir.romanmuhtasarov.domain.entity.Message;
import com.bsuir.romanmuhtasarov.domain.request.MessageRequestTo;
import com.bsuir.romanmuhtasarov.domain.response.MessageResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = NewsMapper.class)
public interface MessageMapper {
    @Mapping(source = "newsId", target = "news.id")
    Message toMessage(MessageRequestTo messageRequestTo);
    @Mapping(source = "news.id", target = "newsId")
    MessageResponseTo toMessageResponseTo(Message message);
}
