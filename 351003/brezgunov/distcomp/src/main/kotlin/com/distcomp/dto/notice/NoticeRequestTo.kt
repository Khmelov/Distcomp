package com.distcomp.dto.notice

data class NoticeRequestTo (
    val id: Long? = null,
    val newsId: Long,
    val content: String
)