package com.example.distcomp.service

import com.example.notecontracts.NoteKafkaTopics
import com.example.notecontracts.NoteReply
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class NoteReplyConsumer(
    private val replyRegistry: NoteReplyRegistry,
    private val projectionSyncService: NoteProjectionStore
) {
    @KafkaListener(
        topics = [NoteKafkaTopics.OUT_TOPIC],
        containerFactory = "noteReplyKafkaListenerContainerFactory"
    )
    fun onReply(reply: NoteReply) {
        projectionSyncService.applyReply(reply)
        reply.correlationId?.let { replyRegistry.complete(it, reply) }
    }
}
