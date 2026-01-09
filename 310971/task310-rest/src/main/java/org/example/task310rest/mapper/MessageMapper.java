package org.example.task310rest.mapper;

import org.example.task310rest.dto.MessageRequestTo;
import org.example.task310rest.dto.MessageResponseTo;
import org.example.task310rest.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "id", ignore = true)
    Message toEntity(MessageRequestTo request);

    MessageResponseTo toDto(Message entity);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(MessageRequestTo request, @MappingTarget Message entity);
}
