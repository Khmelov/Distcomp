package com.distcomp.mapper.topic;

import com.distcomp.dto.topic.TopicCreateRequest;
import com.distcomp.dto.topic.TopicPatchRequest;
import com.distcomp.dto.topic.TopicResponseDto;
import com.distcomp.dto.topic.TopicUpdateRequest;
import com.distcomp.mapper.config.MappedConfig;
import com.distcomp.model.topic.Topic;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MappedConfig.class)
public interface TopicMapper {

    @Mapping(source = "userWhoPostTopicId", target = "userId")
    TopicResponseDto toResponse(Topic entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "userId", target = "userWhoPostTopicId")
    Topic toEntity(TopicCreateRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "userWhoPostTopicId", ignore = true)
    Topic updateFromDto(TopicUpdateRequest dto, @MappingTarget Topic entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "userId", target = "userWhoPostTopicId")
    Topic updateFromPatch(TopicPatchRequest dto, @MappingTarget Topic entity);
}
