package com.distcomp.mapper

import com.distcomp.dto.news.NewsRequestTo
import com.distcomp.dto.news.NewsResponseTo
import com.distcomp.entity.News
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy
import java.time.LocalDateTime

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface NewsMapper {
    @Mapping(target = "userId", source = "user.id")
    fun toNewsResponse(news: News) : NewsResponseTo

    fun toNewsEntity(newsRequestTo: NewsRequestTo): News {
        return News(
            id = newsRequestTo.id,
            title = newsRequestTo.title,
            content = newsRequestTo.content
        )
    }
}