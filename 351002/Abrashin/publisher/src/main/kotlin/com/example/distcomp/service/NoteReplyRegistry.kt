package com.example.distcomp.service

import com.example.notecontracts.NoteReply
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

@Component
class NoteReplyRegistry {
    private val pending = ConcurrentHashMap<String, CompletableFuture<NoteReply>>()

    fun register(correlationId: String): CompletableFuture<NoteReply> {
        val future = CompletableFuture<NoteReply>()
        pending[correlationId] = future
        return future
    }

    fun complete(correlationId: String, reply: NoteReply) {
        pending.remove(correlationId)?.complete(reply)
    }

    fun remove(correlationId: String) {
        pending.remove(correlationId)
    }
}
