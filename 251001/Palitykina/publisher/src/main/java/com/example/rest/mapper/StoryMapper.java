package com.example.rest.mapper;
import com.example.rest.dto.requestDto.StoryRequestTo;
import com.example.rest.dto.responseDto.StoryResponseTo;
import com.example.rest.dto.updateDto.StoryUpdateTo;
import com.example.rest.entity.Label;
import com.example.rest.entity.Story;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface StoryMapper {

    @Mapping(source = "user.id", target = "userId")
    StoryResponseTo ToStoryResponseTo(Story Story);
    @Mapping(source = "labels", target = "labels", qualifiedByName = "namesToLabels")
    Story ToStory(StoryRequestTo storyRequestTo);
    @Mapping(source = "labels", target = "labels", qualifiedByName = "namesToLabels")
    Story ToStory(StoryUpdateTo storyUpdateTo);

    @Named("namesToLabels")
    default List<Label> namesToLabels(List<String> names) {
        return new ArrayList<>();
    }

}
