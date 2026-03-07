package com.example.distcomp.dto.response

import com.fasterxml.jackson.annotation.JsonRootName

@JsonRootName("note")
data class NoteResponseTo(
    var id: Long? = null,
    var tweetId: Long? = null,
    var content: String? = null
)
