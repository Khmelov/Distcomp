package com.example.distcomp.repository

import com.example.distcomp.model.Sticker
import org.springframework.stereotype.Repository

@Repository
class StickerRepository : InMemoryRepository<Sticker>() {
    fun findByName(name: String) = storage.values.find { it.name == name }
}
