package com.example.distcomp.model

import java.time.LocalDateTime

class Tweet(
    id: Long? = null,
    var creatorId: Long? = null,
    var title: String = "",
    var content: String = "",
    var stickerIds: Set<Long> = emptySet(),
    var created: LocalDateTime? = null,
    var modified: LocalDateTime? = null
) : BaseEntity(id)
