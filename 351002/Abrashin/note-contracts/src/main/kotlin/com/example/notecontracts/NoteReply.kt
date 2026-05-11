package com.example.notecontracts

import java.time.Instant

data class NoteReply(
    val correlationId: String? = null,
    val operation: NoteOperation,
    val noteId: Long? = null,
    val tweetId: Long? = null,
    val timestamp: Instant = Instant.now(),
    val success: Boolean,
    val httpStatus: Int,
    val errorCode: Int? = null,
    val message: String? = null,
    val note: NotePayload? = null,
    val notes: List<NotePayload> = emptyList()
)
