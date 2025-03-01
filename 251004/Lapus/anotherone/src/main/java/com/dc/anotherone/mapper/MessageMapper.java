package com.dc.anotherone.mapper;

import com.dc.anotherone.model.blo.Message;
import com.dc.anotherone.model.dto.MessageRequestTo;
import com.dc.anotherone.model.dto.MessageResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.Collection;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MessageMapper {

    Message mapToBlo(MessageRequestTo MessageRequestTo);
    @Mapping(target = "newsId", source = "Message.news.id")
    MessageResponseTo mapToDto(Message Message);

    Collection<Message> mapToListBlo(Collection<MessageRequestTo> kList);

    Collection<MessageResponseTo> mapToListDto(Collection<Message> tList);

}
