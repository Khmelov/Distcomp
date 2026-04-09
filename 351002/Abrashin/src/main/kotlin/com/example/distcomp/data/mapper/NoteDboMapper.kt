package com.example.distcomp.data.mapper

import com.example.distcomp.data.dbo.NoteDbo
import com.example.distcomp.model.Note
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper(componentModel = "spring")
interface NoteDboMapper {
    @Mapping(target = "tweet", ignore = true)
    fun toDbo(model: Note): NoteDbo

    @Mapping(target = "tweetId", source = "tweet.id")
    fun toModel(dbo: NoteDbo): Note
}
