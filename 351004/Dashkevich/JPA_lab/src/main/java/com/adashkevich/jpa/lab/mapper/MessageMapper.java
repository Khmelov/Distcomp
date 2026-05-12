package com.adashkevich.jpa.lab.mapper;

import com.adashkevich.jpa.lab.dto.request.MessageRequestTo;
import com.adashkevich.jpa.lab.dto.response.MessageResponseTo;
import com.adashkevich.jpa.lab.model.Message;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    Message toEntity(MessageRequestTo dto);
    MessageResponseTo toResponse(Message entity);
}
