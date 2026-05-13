package org.rv.lab1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.rv.lab1.domain.Story;
import org.rv.lab1.dto.StoryRequestTo;
import org.rv.lab1.dto.StoryResponseTo;

import java.time.Instant;

@Mapper(componentModel = "spring", imports = {Instant.class})
public interface StoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "editor", ignore = true)
    @Mapping(target = "markers", ignore = true)
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    Story   toEntity(StoryRequestTo dto);

    @Mapping(target = "editorId", source = "editor.id")
    @Mapping(target = "markerIds", expression = "java(story.getMarkers().stream().map(org.rv.lab1.domain.Marker::getId).collect(java.util.stream.Collectors.toSet()))")
    StoryResponseTo toResponse(Story story);

    @Mapping(target = "editor", ignore = true)
    @Mapping(target = "markers", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(Instant.now())")
    void updateEntity(StoryRequestTo dto, @MappingTarget Story target);
}

