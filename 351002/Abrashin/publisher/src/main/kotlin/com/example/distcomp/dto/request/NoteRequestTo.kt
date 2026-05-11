package com.example.distcomp.dto.request

import com.fasterxml.jackson.annotation.JsonRootName
import jakarta.validation.constraints.Size

@JsonRootName("note")
data class NoteRequestTo(
    var tweetId: Long? = null,
    var country: String? = null,
    @field:Size(min = 2, max = 2048)
    var content: String? = null
)
