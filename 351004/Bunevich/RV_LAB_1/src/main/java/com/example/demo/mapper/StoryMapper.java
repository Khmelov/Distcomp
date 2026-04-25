package com.example.demo.mapper;

import com.example.demo.dto.request.StoryRequestTo;
import com.example.demo.dto.response.StoryResponseTo;
import com.example.demo.models.Story;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StoryMapper {
    StoryResponseTo toResponse(Story story);
    Story toEntity(StoryRequestTo request);
    List<StoryResponseTo> storyListToResponseList(List<Story> stories);
}
