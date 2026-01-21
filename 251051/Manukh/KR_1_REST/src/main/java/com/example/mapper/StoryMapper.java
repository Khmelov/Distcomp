// StoryMapper.java
package com.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.example.dto.request.StoryRequestTo;
import com.example.dto.response.StoryResponseTo;
import com.example.model.Story;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "markIds", ignore = true)
    Story toEntity(StoryRequestTo request);

    StoryResponseTo toResponse(Story story);

    List<StoryResponseTo> toResponseList(List<Story> stories);
}