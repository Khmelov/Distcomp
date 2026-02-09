package com.distcomp.dto.news

import com.distcomp.dto.marker.MarkerResponseTo
import com.distcomp.dto.notice.NoticeResponseTo
import com.distcomp.dto.user.UserResponseTo
import java.time.LocalDateTime

data class NewsResponseTo(
    val id: Long,
    val title: String,
    val content: String,
    val created: LocalDateTime,
    val modified: LocalDateTime,
    val user: UserResponseTo,
    val notices: MutableList<NoticeResponseTo>,
    val markers: MutableList<MarkerResponseTo>
)
