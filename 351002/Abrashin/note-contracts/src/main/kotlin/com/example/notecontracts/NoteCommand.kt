package com.example.notecontracts

import java.time.Instant

data class NoteCommand(
    val correlationId: String,
    val operation: NoteOperation,
    val noteId: Long? = null,
    val tweetId: Long? = null,
    val timestamp: Instant = Instant.now(),
    val country: String? = null,
    val content: String? = null,
    val page: Int? = null,
    val size: Int? = null,
    val sort: List<String> = emptyList()
)
