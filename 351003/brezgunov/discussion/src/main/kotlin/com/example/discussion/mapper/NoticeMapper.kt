package com.example.discussion.mapper

import com.example.discussion.dto.notice.NoticeRequestTo
import com.example.discussion.dto.notice.NoticeResponseTo
import com.example.discussion.entity.Notice
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface NoticeMapper {
    fun toNoticeResponse(notice: Notice) : NoticeResponseTo

    fun toNoticeEntity(noticeRequestTo: NoticeRequestTo) : Notice
}