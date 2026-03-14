package com.example.Labs.mapper;

import com.example.Labs.dto.request.MessageRequestTo;
import com.example.Labs.dto.response.MessageResponseTo;
import com.example.Labs.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "id", ignore = true)
    Message toEntity(MessageRequestTo dto);

    MessageResponseTo toDto(Message entity);

    @Mapping(target = "id", ignore = true)
    void updateEntity(MessageRequestTo dto, @MappingTarget Message entity);
}
