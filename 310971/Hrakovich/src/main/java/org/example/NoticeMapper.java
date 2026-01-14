package org.example;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoticeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "story", ignore = true)
    Notice toEntity(NoticeRequestTo dto);

    @Mapping(target = "storyId", source = "story.id")
    NoticeResponseTo toResponse(Notice entity);
}