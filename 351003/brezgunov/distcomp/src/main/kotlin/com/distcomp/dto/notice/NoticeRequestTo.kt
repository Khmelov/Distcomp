package com.distcomp.dto.notice

data class NoticeRequestTo (
    var id: Long? = null,
    var newsId: Long,
    var content: String
)