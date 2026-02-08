package com.distcomp.dto.news

import com.distcomp.dto.marker.MarkerResponseTo
import com.distcomp.dto.notice.NoticeResponseTo
import com.distcomp.dto.user.UserResponseTo
import java.time.LocalDateTime

data class NewsResponseTo(
    var id: Long,
    var title: String,
    var content: String,
    var created: LocalDateTime,
    var modified: LocalDateTime,
    var user: UserResponseTo,
    var notices: MutableList<NoticeResponseTo>,
    var markers: MutableList<MarkerResponseTo>
)
