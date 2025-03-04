package com.bsuir.dc.util.mapper;

import com.bsuir.dc.dto.request.TopicRequestTo;
import com.bsuir.dc.dto.response.TopicResponseTo;
import com.bsuir.dc.model.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TopicMapper {
    @Mapping(target = "authorId", expression = "java(topic.getAuthor().getId())")
    @Mapping(target = "labels", expression = "java(topic.getLabels().stream().map(label -> label.getName()).toList())")
    TopicResponseTo toTopicResponse(Topic topic);

    List<TopicResponseTo> toTopicResponseList(List<Topic> topics);

    @Mapping(target = "labels", ignore = true)
    Topic toTopic(TopicRequestTo topicRequestTo);
}
