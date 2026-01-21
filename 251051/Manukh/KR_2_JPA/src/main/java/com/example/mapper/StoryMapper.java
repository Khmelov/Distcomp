package com.example.mapper;

import com.example.dto.request.StoryRequestTo;
import com.example.dto.response.StoryResponseTo;
import com.example.entity.Story;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "editor", ignore = true)
    @Mapping(target = "marks", ignore = true)
    @Mapping(target = "reactions", ignore = true)
    Story toEntity(StoryRequestTo request);

    @Mapping(source = "editor.id", target = "editorId")
    @Mapping(source = "marks", target = "markIds")
    @Mapping(source = "createdAt", target = "created")
    @Mapping(source = "modifiedAt", target = "modified")
    StoryResponseTo toResponse(Story story);

    default Set<Long> mapMarks(Set<com.example.entity.Mark> marks) {
        if (marks == null) {
            return new HashSet<>();
        }
        return marks.stream()
                .map(com.example.entity.Mark::getId)
                .collect(Collectors.toSet());
    }

    List<StoryResponseTo> toResponseList(List<Story> stories);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "editor", ignore = true)
    @Mapping(target = "marks", ignore = true)
    @Mapping(target = "reactions", ignore = true)
    void updateEntity(StoryRequestTo request, @MappingTarget Story story);
}