package com.distcomp.mapper

import com.distcomp.dto.notice.NoticeRequestTo
import com.distcomp.dto.notice.NoticeResponseTo
import com.distcomp.entity.Notice
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface NoticeMapper {
    @Mapping(source = "news.id", target = "newsId")
    fun toNoticeResponse(notice: Notice) : NoticeResponseTo

    fun toNoticeEntity(noticeRequestTo: NoticeRequestTo) : Notice
}