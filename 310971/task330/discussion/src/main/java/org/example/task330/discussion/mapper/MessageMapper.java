package org.example.task330.discussion.mapper;

import org.example.task330.discussion.dto.MessageRequestTo;
import org.example.task330.discussion.dto.MessageResponseTo;
import org.example.task330.discussion.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "key", ignore = true)
    @Mapping(target = "content", source = "content")
    Message toEntity(MessageRequestTo request);

    @Mapping(target = "tweetId", expression = "java(entity.getKey() != null ? entity.getKey().getTweetId() : null)")
    @Mapping(target = "country", expression = "java(entity.getKey() != null ? entity.getKey().getCountry() : null)")
    @Mapping(target = "id", expression = "java(entity.getKey() != null ? entity.getKey().getId() : null)")
    @Mapping(target = "content", source = "content")
    MessageResponseTo toDto(Message entity);

    @Mapping(target = "key", ignore = true)
    @Mapping(target = "content", source = "content")
    void updateEntityFromDto(MessageRequestTo request, @MappingTarget Message entity);
}

