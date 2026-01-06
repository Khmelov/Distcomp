package com.labs.service.mapper;

import com.labs.domain.entity.Message;
import com.labs.service.dto.MessageDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(source = "tweet.id", target = "tweetId")
    MessageDto toDto(Message message);

    @Mapping(target = "tweet", ignore = true)
    Message toEntity(MessageDto messageDto);
}

