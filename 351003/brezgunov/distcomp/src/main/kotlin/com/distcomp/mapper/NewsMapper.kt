package com.distcomp.mapper

import com.distcomp.dto.news.NewsRequestTo
import com.distcomp.dto.news.NewsResponseTo
import com.distcomp.entity.News
import org.mapstruct.Mapper
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface NewsMapper {
    fun toNewsResponse(news: News) : NewsResponseTo

    fun toNewsEntity(newsRequestTo: NewsRequestTo) : News
}