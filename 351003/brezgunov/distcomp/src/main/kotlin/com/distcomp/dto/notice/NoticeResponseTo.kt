package com.distcomp.dto.notice

import java.io.Serializable

data class NoticeResponseTo (
    val id: Long = 0,
    val content: String = "",
    val newsId: Long = 0,
) : Serializable