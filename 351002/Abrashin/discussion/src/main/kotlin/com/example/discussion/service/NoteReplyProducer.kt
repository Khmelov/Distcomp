package com.example.discussion.service

import com.example.discussion.model.NoteEntity
import com.example.notecontracts.NoteKafkaTopics
import com.example.notecontracts.NoteOperation
import com.example.notecontracts.NotePayload
import com.example.notecontracts.NoteReply
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class NoteReplyProducer(
    private val kafkaTemplate: KafkaTemplate<String, NoteReply>
) {
    fun send(key: String, reply: NoteReply) {
        kafkaTemplate.send(NoteKafkaTopics.OUT_TOPIC, key, reply)
    }

    fun sendSync(operation: NoteOperation, note: NoteEntity?, httpStatus: Int, noteId: Long? = null, tweetId: Long? = null) {
        val key = (note?.key?.tweetId ?: tweetId)?.toString() ?: "notes-all"
        send(
            key,
            NoteReply(
                correlationId = null,
                operation = operation,
                noteId = note?.key?.id ?: noteId,
                tweetId = note?.key?.tweetId ?: tweetId,
                success = true,
                httpStatus = httpStatus,
                note = note?.asPayload()
            )
        )
    }

    fun toPayload(note: NoteEntity): NotePayload = note.asPayload()

    private fun NoteEntity.asPayload(): NotePayload = NotePayload(
        id = key.id,
        tweetId = key.tweetId,
        country = country,
        content = content,
        state = state
    )
}
