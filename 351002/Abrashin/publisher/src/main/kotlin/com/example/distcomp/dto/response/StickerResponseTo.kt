package com.example.distcomp.dto.response

import com.fasterxml.jackson.annotation.JsonRootName

@JsonRootName("sticker")
data class StickerResponseTo(
    var id: Long? = null,
    var name: String? = null
)
