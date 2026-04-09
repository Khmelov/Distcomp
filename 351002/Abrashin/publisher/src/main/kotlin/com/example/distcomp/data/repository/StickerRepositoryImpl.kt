package com.example.distcomp.data.repository

import com.example.distcomp.data.datasource.sticker.StickerDataSource
import com.example.distcomp.data.dbo.StickerDbo
import com.example.distcomp.model.Sticker
import org.springframework.stereotype.Repository

import com.example.distcomp.repository.StickerRepository

@Repository
class StickerRepositoryImpl(
    private val dataSource: StickerDataSource
) : DataSourceRepository<Sticker, StickerDbo>(dataSource), StickerRepository {
    override fun findByName(name: String): Sticker? = dataSource.findByName(name)
}
