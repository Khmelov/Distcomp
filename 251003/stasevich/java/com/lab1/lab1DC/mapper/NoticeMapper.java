package com.lab1.lab1DC.mapper;

import com.lab1.lab1DC.dto.NoticeRequestTo;
import com.lab1.lab1DC.dto.NoticeResponseTo;
import com.lab1.lab1DC.entity.Notice;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NoticeMapper {

    NoticeMapper INSTANCE = Mappers.getMapper(NoticeMapper.class);

    Notice toEntity(NoticeRequestTo noticeRequestTo);

    NoticeResponseTo toResponseDto(Notice notice);

    Notice toEntity(NoticeResponseTo noticeResponseTo);

    NoticeRequestTo toRequestDto(Notice notice);
}
