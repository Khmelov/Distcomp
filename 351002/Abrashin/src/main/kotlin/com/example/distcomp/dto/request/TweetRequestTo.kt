package com.example.distcomp.dto.request

import com.fasterxml.jackson.annotation.JsonRootName
import jakarta.validation.constraints.Size

@JsonRootName("tweet")
data class TweetRequestTo(
    var creatorId: Long? = null,
    @field:Size(min = 2, max = 64)
    var title: String? = null,
    @field:Size(min = 2, max = 2048)
    var content: String? = null
)
