package com.example.distcomp.mapper;

import com.example.distcomp.dto.MessageRequestTo;
import com.example.distcomp.dto.MessageResponseTo;
import com.example.distcomp.model.Message;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    Message toEntity(MessageRequestTo request);

    @Mapping(target = "issueId", source = "entity.issue.id")
    MessageResponseTo toResponse(Message entity);

    List<Message> listToEntity(List<MessageRequestTo> dtoList);

    List<MessageResponseTo> listToResponse(List<Message> entityList);

}
