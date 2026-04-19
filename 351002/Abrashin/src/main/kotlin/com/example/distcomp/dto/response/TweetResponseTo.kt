package com.example.distcomp.dto.response

import com.fasterxml.jackson.annotation.JsonRootName
import java.time.LocalDateTime

@JsonRootName("tweet")
data class TweetResponseTo(
    var id: Long? = null,
    var creatorId: Long? = null,
    var title: String? = null,
    var content: String? = null,
    var created: LocalDateTime? = null,
    var modified: LocalDateTime? = null
)
