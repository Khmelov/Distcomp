package com.example.discussion.config

import com.example.notecontracts.NoteCommand
import com.example.notecontracts.NoteKafkaTopics
import com.example.notecontracts.NoteOperation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer

class NoteKafkaConfigTest {

    @Test
    fun `note command round-trips through kafka json serializers`() {
        val objectMapper = Jackson2ObjectMapperBuilder.json().build<com.fasterxml.jackson.databind.ObjectMapper>()
        val serializer = JsonSerializer<NoteCommand>(objectMapper.copy()).apply {
            setAddTypeInfo(false)
        }
        val deserializer = JsonDeserializer(NoteCommand::class.java, objectMapper.copy(), false).apply {
            addTrustedPackages("*")
            setUseTypeHeaders(false)
        }
        val command = NoteCommand(
            correlationId = "corr-1",
            operation = NoteOperation.CREATE,
            tweetId = 7L,
            country = "BY",
            content = "hello",
            sort = listOf("id", "desc")
        )

        val payload = serializer.serialize(NoteKafkaTopics.IN_TOPIC, command)
        val restored = deserializer.deserialize(NoteKafkaTopics.IN_TOPIC, payload)

        assertEquals(command, restored)
    }
}
