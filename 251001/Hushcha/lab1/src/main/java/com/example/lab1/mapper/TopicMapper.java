package com.example.lab1.mapper;

import com.example.lab1.dto.TopicRequestTo;
import com.example.lab1.dto.TopicResponseTo;
import com.example.lab1.entity.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TopicMapper {
    TopicMapper INSTANCE = Mappers.getMapper(TopicMapper.class);

    TopicResponseTo toDTO(Topic topic);
    Topic toEntity(TopicRequestTo topicRequestTo);
}
