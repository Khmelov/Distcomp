package com.example.distcomp.dto.request

import com.fasterxml.jackson.annotation.JsonRootName
import jakarta.validation.constraints.Size

@JsonRootName("sticker")
data class StickerRequestTo(
    @field:Size(min = 2, max = 32)
    var name: String? = null
)
