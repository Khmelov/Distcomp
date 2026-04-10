package com.example.distcomp.service

import com.example.distcomp.data.datasource.note.local.NoteProjectionJpaRepository
import com.example.distcomp.data.dbo.NoteProjectionDbo
import com.example.notecontracts.NoteOperation
import com.example.notecontracts.NotePayload
import com.example.notecontracts.NoteReply
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class NoteProjectionSyncService(
    private val repository: NoteProjectionJpaRepository
) : NoteProjectionStore {
    override
    fun markPending(id: Long, tweetId: Long) {
        repository.save(NoteProjectionDbo(id = id, tweetId = tweetId))
    }

    override
    fun requireRoute(id: Long): NoteProjectionDbo =
        repository.findById(id).orElseThrow { com.example.distcomp.exception.NotFoundException("Note with id $id not found") }

    override
    fun applyReply(reply: NoteReply) {
        if (!reply.success) {
            return
        }

        when (reply.operation) {
            NoteOperation.DELETE -> reply.noteId?.let(repository::deleteById)
            NoteOperation.CREATE,
            NoteOperation.PUT,
            NoteOperation.PATCH,
            NoteOperation.GET_BY_ID -> reply.note?.let(::upsert)
            NoteOperation.GET_ALL,
            NoteOperation.GET_BY_TWEET_ID -> reply.notes.forEach(::upsert)
        }
    }

    private fun upsert(payload: NotePayload) {
        val id = payload.id ?: return
        val tweetId = payload.tweetId ?: return
        val state = payload.state ?: return
        repository.save(
            NoteProjectionDbo(
                id = id,
                tweetId = tweetId,
                state = state,
                updatedAt = LocalDateTime.now()
            )
        )
    }
}
