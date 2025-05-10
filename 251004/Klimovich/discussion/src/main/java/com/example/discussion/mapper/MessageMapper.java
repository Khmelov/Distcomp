package com.example.discussion.mapper;

import com.example.discussion.dto.MessageRequestTo;
import com.example.discussion.dto.MessageResponseTo;
import com.example.discussion.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "country",source = "id", qualifiedByName = "createCountry")
    Message toEntity(MessageRequestTo request);

    MessageResponseTo toResponse(Message entity);

    List<Message> listToEntity(List<MessageRequestTo> dtoList);

    List<MessageResponseTo> listToResponse(List<Message> entityList);

    @Named("createCountry")
    default String map(long value){
        return "bl";
    }
}
