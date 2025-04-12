package by.yelkin.TopicService.mapping;

import by.yelkin.TopicService.dto.topic.TopicRq;
import by.yelkin.TopicService.dto.topic.TopicRs;
import by.yelkin.TopicService.dto.topic.TopicUpdateRq;
import by.yelkin.TopicService.entity.Topic;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TopicMapper {
    @Mapping(target = "creator.id", source = "creatorId")
    Topic fromDto(TopicRq rq);

    @Mapping(source = "creator.id", target = "creatorId")
    TopicRs toDto(Topic topic);

    List<TopicRs> toDtoList(List<Topic> topics);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateTopic(@MappingTarget Topic topic, TopicUpdateRq rq);
}
