package com.example.distcomp.mapper

import com.example.distcomp.dto.request.StickerRequestTo
import com.example.distcomp.dto.response.StickerResponseTo
import com.example.distcomp.model.Sticker
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface StickerMapper {
    fun toEntity(request: StickerRequestTo): Sticker
    fun toResponse(entity: Sticker): StickerResponseTo
}
