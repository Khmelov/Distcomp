package org.example.task310rest.mapper;

import org.example.task310rest.dto.MessageRequestTo;
import org.example.task310rest.dto.MessageResponseTo;
import org.example.task310rest.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    Message toEntity(MessageRequestTo request);

    MessageResponseTo toDto(Message entity);

    void updateEntityFromDto(MessageRequestTo request, @MappingTarget Message entity);
}


