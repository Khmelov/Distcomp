package com.example.distcomp.service

import com.example.distcomp.exception.NoteRequestTimeoutException
import com.example.notecontracts.NoteCommand
import com.example.notecontracts.NoteReply
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@Service
class NoteKafkaGateway(
    private val kafkaTemplate: KafkaTemplate<String, NoteCommand>,
    private val replyRegistry: NoteReplyRegistry,
    @Value("\${note.kafka.request-timeout}") private val requestTimeout: Duration
) : PublisherNoteGateway {
    override
    fun send(commandKey: String, command: NoteCommand) {
        kafkaTemplate.send(com.example.notecontracts.NoteKafkaTopics.IN_TOPIC, commandKey, command)
    }

    override
    fun sendAndAwait(commandKey: String, command: NoteCommand): NoteReply {
        val future = replyRegistry.register(command.correlationId)
        kafkaTemplate.send(com.example.notecontracts.NoteKafkaTopics.IN_TOPIC, commandKey, command)
        return try {
            future.get(requestTimeout.toMillis(), TimeUnit.MILLISECONDS)
        } catch (_: TimeoutException) {
            throw NoteRequestTimeoutException("Timed out waiting for note response")
        } finally {
            replyRegistry.remove(command.correlationId)
        }
    }
}
