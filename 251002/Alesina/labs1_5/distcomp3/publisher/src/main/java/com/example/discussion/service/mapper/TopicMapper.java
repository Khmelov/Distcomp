package com.example.discussion.service.mapper;

import com.example.discussion.dto.TopicRequestTo;
import com.example.discussion.dto.TopicResponseTo;
import com.example.discussion.model.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TopicMapper {
    @Mapping(target = "stickers", ignore = true)
    Topic toEntity(TopicRequestTo dto);
    @Mapping(source = "user.id", target = "userId")
    TopicResponseTo toDto(Topic entity);
    @Mapping(target = "stickers", ignore = true)
    void updateEntityFromDto(TopicRequestTo dto, @MappingTarget Topic entity);
}