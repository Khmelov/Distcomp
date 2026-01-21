package com.example.mapper;

import com.example.dto.request.MarkRequestTo;
import com.example.dto.response.MarkResponseTo;
import com.example.entity.Mark;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MarkMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "stories", ignore = true)
    Mark toEntity(MarkRequestTo request);

    @Mapping(source = "stories", target = "storyIds")
    @Mapping(source = "createdAt", target = "created")
    @Mapping(source = "modifiedAt", target = "modified")
    MarkResponseTo toResponse(Mark mark);

    default Set<Long> mapStories(Set<com.example.entity.Story> stories) {
        if (stories == null) {
            return new HashSet<>();
        }
        return stories.stream()
                .map(com.example.entity.Story::getId)
                .collect(Collectors.toSet());
    }

    List<MarkResponseTo> toResponseList(List<Mark> marks);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "stories", ignore = true)
    void updateEntity(MarkRequestTo request, @MappingTarget Mark mark);
}