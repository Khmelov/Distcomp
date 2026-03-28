package com.example.task310.mapper;

import com.example.task310.dto.NoticeRequestTo;
import com.example.task310.dto.NoticeResponseTo;
import com.example.task310.model.Notice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NoticeMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "modified", ignore = true)
    @Mapping(target = "news", ignore = true)
    Notice toEntity(NoticeRequestTo request);

    @Mapping(source = "news.id", target = "newsId")
    NoticeResponseTo toResponse(Notice notice);
}