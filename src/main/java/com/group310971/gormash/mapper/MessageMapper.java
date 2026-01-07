package com.group310971.gormash.mapper;

import com.group310971.gormash.dto.MessageRequestTo;
import com.group310971.gormash.dto.MessageResponseTo;
import com.group310971.gormash.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessageMapper {
    MessageMapper INSTANCE = Mappers.getMapper(MessageMapper.class);

    Message toEntity(MessageRequestTo dto);

    @Mapping(target = "topicId", source = "topic.id")
    MessageResponseTo toResponse(Message entity);
}
