package com.example.demo.mapper;

import com.example.demo.dto.request.StoryRequestTo;
import com.example.demo.dto.response.StoryResponseTo;
import com.example.demo.models.Story;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StoryMapper {
    @Mapping(target = "writerId", source = "writer.id")
    @Mapping(target = "tags", source = "tags")
    StoryResponseTo toResponse(Story story);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "writer", ignore = true)
    @Mapping(target = "tags", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    Story toEntity(StoryRequestTo request);

    List<StoryResponseTo> storyListToResponseList(List<Story> stories);
}
