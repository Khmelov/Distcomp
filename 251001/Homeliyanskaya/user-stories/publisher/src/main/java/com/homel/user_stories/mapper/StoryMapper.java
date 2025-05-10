package com.homel.user_stories.mapper;

import com.homel.user_stories.dto.StoryRequestTo;
import com.homel.user_stories.dto.StoryResponseTo;
import com.homel.user_stories.model.Story;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StoryMapper {
    StoryMapper INSTANCE = Mappers.getMapper(StoryMapper.class);

    Story toEntity(StoryRequestTo dto);
    default StoryResponseTo toResponse(Story story) {
        StoryResponseTo response = new StoryResponseTo();
        response.setId(story.getId());
        response.setTitle(story.getTitle());
        response.setContent(story.getContent());
        response.setUserId(story.getUser().getId());
        return response;
    }
}
