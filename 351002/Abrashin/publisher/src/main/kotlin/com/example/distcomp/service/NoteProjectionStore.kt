package com.example.distcomp.service

import com.example.distcomp.data.dbo.NoteProjectionDbo
import com.example.notecontracts.NoteReply

interface NoteProjectionStore {
    fun markPending(id: Long, tweetId: Long)
    fun requireRoute(id: Long): NoteProjectionDbo
    fun applyReply(reply: NoteReply)
}
