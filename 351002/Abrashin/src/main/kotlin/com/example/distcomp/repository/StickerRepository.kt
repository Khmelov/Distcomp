package com.example.distcomp.repository

import com.example.distcomp.model.Sticker

interface StickerRepository : CrudRepository<Sticker> {
    fun findByName(name: String): Sticker?
}
