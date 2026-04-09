package com.example.distcomp.mapper

import com.example.distcomp.dto.request.TweetRequestTo
import com.example.distcomp.dto.response.TweetResponseTo
import com.example.distcomp.model.Tweet
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named

@Mapper(componentModel = "spring", uses = [StickerMapper::class])
interface TweetMapper {
    @Mapping(target = "stickers", source = "stickers", qualifiedByName = ["namesToStickers"])
    fun toEntity(request: TweetRequestTo): Tweet
    
    fun toResponse(entity: Tweet): TweetResponseTo

    @org.mapstruct.Named("namesToStickers")
    fun namesToStickers(names: List<String>?): List<com.example.distcomp.model.Sticker> {
        return names?.map { com.example.distcomp.model.Sticker(name = it) } ?: emptyList()
    }
}
