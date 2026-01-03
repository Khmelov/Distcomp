package com.task.rest.mapper;

import com.task.rest.dto.NoticeRequestTo;
import com.task.rest.dto.NoticeResponseTo;
import com.task.rest.model.Notice;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface NoticeMapper {
    Notice toEntity(NoticeRequestTo dto);
    NoticeResponseTo toDto(Notice entity);
    List<NoticeResponseTo> toDtoList(List<Notice> entities);
}