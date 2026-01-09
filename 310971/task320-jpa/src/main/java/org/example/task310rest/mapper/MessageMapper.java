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
    @Mapping(target = "tweet", ignore = true)
    Message toEntity(MessageRequestTo request);

    @Mapping(target = "tweetId", expression = "java(entity.getTweet() != null ? entity.getTweet().getId() : null)")
    MessageResponseTo toDto(Message entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tweet", ignore = true)
    void updateEntityFromDto(MessageRequestTo request, @MappingTarget Message entity);
}


