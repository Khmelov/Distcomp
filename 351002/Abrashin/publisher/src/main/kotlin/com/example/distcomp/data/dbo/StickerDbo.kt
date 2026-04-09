package com.example.distcomp.data.dbo

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "tbl_sticker")
class StickerDbo : BaseDbo() {
    @Column(unique = true, nullable = false)
    var name: String = ""
}
