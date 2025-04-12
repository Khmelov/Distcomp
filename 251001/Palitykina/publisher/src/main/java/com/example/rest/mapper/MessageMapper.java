package com.example.rest.mapper;
import com.example.rest.dto.requestDto.MessageRequestTo;
import com.example.rest.dto.responseDto.MessageResponseTo;
import com.example.rest.dto.updateDto.MessageUpdateTo;
import com.example.rest.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    MessageResponseTo ToMessageResponseTo(Message Message);
    Message ToMessage(MessageRequestTo MessageRequestTo);
    Message ToMessage(MessageUpdateTo MessageUpdateTo);
}
