package com.distcomp.entity

import java.time.LocalDateTime

class News(
    var id: Long,
    var title: String,
    var content: String,
    var created: LocalDateTime,
    var modified: LocalDateTime,
    var user: User,
    var notices: MutableList<Notice>,
    var markers: MutableList<Marker>
)