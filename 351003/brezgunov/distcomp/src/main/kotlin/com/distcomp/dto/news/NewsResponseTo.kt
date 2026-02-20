package com.distcomp.dto.news

import java.time.LocalDateTime

data class NewsResponseTo(
    val id: Long,
    val title: String,
    val content: String,
    val created: LocalDateTime,
    val modified: LocalDateTime,
    val userId: Long
)
