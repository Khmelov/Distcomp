package com.example.distcomp.data.mapper

import com.example.distcomp.data.dbo.StickerDbo
import com.example.distcomp.model.Sticker
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface StickerDboMapper {
    fun toDbo(model: Sticker): StickerDbo
    fun toModel(dbo: StickerDbo): Sticker
}
