package com.example.rest.mapper;

import com.example.rest.dto.TopicRequestTo;
import com.example.rest.dto.TopicResponseTo;
import com.example.rest.entity.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface TopicMapper {
    Topic toEntity(TopicRequestTo topicRequestTo);
    TopicResponseTo toResponse(Topic topic);
}