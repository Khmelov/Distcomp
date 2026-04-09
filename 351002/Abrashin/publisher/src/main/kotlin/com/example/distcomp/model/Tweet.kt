package com.example.distcomp.model

import java.time.LocalDateTime

data class Tweet(
    var creatorId: Long? = null,
    var title: String? = null,
    var content: String? = null,
    var created: LocalDateTime? = null,
    var modified: LocalDateTime? = null,
    var stickers: List<Sticker> = emptyList()
) : BaseEntity()
