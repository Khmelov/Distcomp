package com.example.Task310.mapper;

import com.example.Task310.bean.Story;
import com.example.Task310.dto.StoryRequestTo;
import com.example.Task310.dto.StoryResponseTo;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {MarkerMapper.class})
public interface StoryMapper {
    @Mapping(source = "editor.id", target = "editorId")
    StoryResponseTo toResponse(Story story);

    @Mapping(target = "editor", ignore = true)
    @Mapping(target = "markers", ignore = true)
    @Mapping(target = "posts", ignore = true)
    Story toEntity(StoryRequestTo request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "editor", ignore = true)
    @Mapping(target = "markers", ignore = true)
    @Mapping(target = "posts", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    void updateEntity(StoryRequestTo request, @MappingTarget Story story);
}