package com.example.Task310.mapper;

import com.example.Task310.bean.Story;
import com.example.Task310.dto.StoryRequestTo;
import com.example.Task310.dto.StoryResponseTo;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface StoryMapper {
    Story toEntity(StoryRequestTo dto);
    StoryResponseTo toDto(Story entity);
    void updateEntityFromDto(StoryRequestTo dto, @MappingTarget Story entity);
}