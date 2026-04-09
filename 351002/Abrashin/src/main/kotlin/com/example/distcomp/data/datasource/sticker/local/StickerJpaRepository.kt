package com.example.distcomp.data.datasource.sticker.local

import com.example.distcomp.data.dbo.StickerDbo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface StickerJpaRepository : JpaRepository<StickerDbo, Long>, JpaSpecificationExecutor<StickerDbo> {
    fun findByName(name: String): Optional<StickerDbo>
}
