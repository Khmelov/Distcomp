package com.example.notecontracts

data class NotePayload(
    val id: Long? = null,
    val tweetId: Long? = null,
    val country: String? = null,
    val content: String? = null,
    val state: NoteState? = null
)
