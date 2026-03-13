package com.example.distcomp.mapper

import com.example.distcomp.dto.request.TweetRequestTo
import com.example.distcomp.dto.response.TweetResponseTo
import com.example.distcomp.model.Tweet
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface TweetMapper {
    fun toEntity(request: TweetRequestTo): Tweet
    fun toResponse(entity: Tweet): TweetResponseTo
}
