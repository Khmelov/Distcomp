package com.adashkevich.rest.lab.mapper;

import com.adashkevich.rest.lab.dto.request.MessageRequestTo;
import com.adashkevich.rest.lab.dto.response.MessageResponseTo;
import com.adashkevich.rest.lab.model.Message;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    Message toEntity(MessageRequestTo dto);
    MessageResponseTo toResponse(Message entity);
}
