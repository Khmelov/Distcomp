package com.distcomp.dto.notice

data class NoticeResponseTo (
    val id: Long,
    val country: String,
    val content: String,
    val newsId: Long,
)