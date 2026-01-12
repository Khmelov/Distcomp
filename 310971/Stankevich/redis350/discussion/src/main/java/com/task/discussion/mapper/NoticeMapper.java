package com.task.discussion.mapper;

import com.task.discussion.dto.NoticeRequestTo;
import com.task.discussion.dto.NoticeResponseTo;
import com.task.discussion.model.Notice;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NoticeMapper {

    NoticeResponseTo toResponseTo(Notice notice);

    Notice toEntity(NoticeRequestTo requestTo);
}