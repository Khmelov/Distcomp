package com.example.discussion.dto.notice

import java.util.UUID

data class NoticeResponseTo (
    val id: UUID,
    val country: String,
    val content: String,
    val newsId: Long,
)