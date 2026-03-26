package com.example.forum.mapper;

import com.example.forum.dto.request.TopicRequestTo;
import com.example.forum.dto.request.UserRequestTo;
import com.example.forum.dto.response.TopicResponseTo;
import com.example.forum.entity.Topic;
import com.example.forum.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TopicMapper {

    @Mapping(target = "id", ignore = true)
    Topic toEntity(TopicRequestTo dto);

    TopicResponseTo toResponse(Topic entity);
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(TopicRequestTo dto, @MappingTarget Topic entity);
}
