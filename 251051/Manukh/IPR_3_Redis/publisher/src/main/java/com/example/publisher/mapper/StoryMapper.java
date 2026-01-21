package com.example.publisher.mapper;

import com.example.publisher.dto.request.StoryRequestTo;
import com.example.publisher.dto.response.StoryResponseTo;
import com.example.publisher.entity.Story;
import com.example.publisher.entity.Mark;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StoryMapper {

    @Mapping(source = "editor.id", target = "editorId")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "modifiedAt", target = "modifiedAt")
    @Mapping(source = "marks", target = "markIds")
    StoryResponseTo toResponse(Story story);

    @Mapping(source = "editorId", target = "editor.id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "marks", ignore = true)
    @Mapping(target = "reactions", ignore = true)
    Story toEntity(StoryRequestTo request);

    List<StoryResponseTo> toResponseList(List<Story> stories);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "marks", ignore = true)
    @Mapping(target = "reactions", ignore = true)
    @Mapping(target = "editor", ignore = true)
    void updateEntity(StoryRequestTo request, @MappingTarget Story story);

    default List<Long> mapMarksToIds(Set<Mark> marks) {
        if (marks == null) {
            return null;
        }
        return marks.stream()
                .map(Mark::getId)
                .collect(Collectors.toList());
    }
}