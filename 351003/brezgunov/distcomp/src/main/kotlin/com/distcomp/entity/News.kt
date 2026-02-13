package com.distcomp.entity

import java.time.LocalDateTime

class News(
    var id: Long? = null,
    var title: String,
    var content: String,
    var created: LocalDateTime = LocalDateTime.now(),
    var modified: LocalDateTime = LocalDateTime.now(),
    var user: User? = null
)