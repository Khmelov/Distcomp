package com.example.distcomp.data.mapper

import com.example.distcomp.data.dbo.TweetDbo
import com.example.distcomp.model.Tweet
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring", uses = [StickerDboMapper::class])
interface TweetDboMapper {
    @Mapping(target = "creator", ignore = true)
    fun toDbo(model: Tweet): TweetDbo

    @Mapping(target = "creatorId", source = "creator.id")
    fun toModel(dbo: TweetDbo): Tweet
}
