package com.example.distcomp.data.datasource.sticker

import com.example.distcomp.data.datasource.BaseDataSource
import com.example.distcomp.data.dbo.StickerDbo
import com.example.distcomp.model.Sticker

interface StickerDataSource : BaseDataSource<Sticker, StickerDbo> {
    fun findByName(name: String): Sticker?
}
