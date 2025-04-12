package com.lab1.lab1DC.mapper;

import com.lab1.lab1DC.dto.StoryRequestTo;
import com.lab1.lab1DC.dto.StoryResponseTo;
import com.lab1.lab1DC.entity.Story;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StoryMapper {

    StoryMapper INSTANCE = Mappers.getMapper(StoryMapper.class);

    Story toEntity(StoryRequestTo storyRequestTo);

    StoryResponseTo toResponseDto(Story story);

    Story toEntity(StoryResponseTo storyResponseTo);

    StoryRequestTo toRequestDto(Story story);
}
