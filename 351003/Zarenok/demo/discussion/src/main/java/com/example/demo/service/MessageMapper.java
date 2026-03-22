package com.example.demo.service;


import com.example.demo.dto.requests.MessageRequestTo;
import com.example.demo.dto.responses.MessageResponseTo;
import com.example.demo.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "key", expression = "java(new MessageKey(dto.getIssueId(), UUID.randomUUID()))")
    @Mapping(target = "content", source = "content")
    Message toEntity(MessageRequestTo dto);

    @Mapping(source = "key.id", target = "id")
    @Mapping(source = "key.issueId", target = "issueId")
    MessageResponseTo toResponse(Message entity);

    @Mapping(target = "key", ignore = true)
    void updateEntity(MessageRequestTo dto, @MappingTarget Message entity);
}
