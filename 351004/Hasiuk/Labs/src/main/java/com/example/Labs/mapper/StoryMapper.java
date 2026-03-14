package com.example.Labs.mapper;

import com.example.Labs.dto.request.StoryRequestTo;
import com.example.Labs.dto.response.StoryResponseTo;
import com.example.Labs.entity.Story;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Story toEntity(StoryRequestTo dto);

    StoryResponseTo toDto(Story entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    void updateEntity(StoryRequestTo dto, @MappingTarget Story entity);
}