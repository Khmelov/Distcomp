package com.homel.user_stories.mapper;

import com.homel.user_stories.dto.NoticeRequestTo;
import com.homel.user_stories.dto.NoticeResponseTo;
import com.homel.user_stories.dto.StoryResponseTo;
import com.homel.user_stories.model.Notice;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NoticeMapper {
    NoticeMapper INSTANCE = Mappers.getMapper(NoticeMapper.class);

    Notice toEntity(NoticeRequestTo dto);
    default NoticeResponseTo toResponse(Notice notice) {
        NoticeResponseTo response = new NoticeResponseTo();
        response.setId(notice.getId());
        response.setContent(notice.getContent());
        response.setStoryId(notice.getStory().getId());
        return response;
    }
}
