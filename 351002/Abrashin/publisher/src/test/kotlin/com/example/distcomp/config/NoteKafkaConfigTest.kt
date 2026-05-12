package com.example.distcomp.config

import com.example.notecontracts.NoteKafkaTopics
import com.example.notecontracts.NoteOperation
import com.example.notecontracts.NotePayload
import com.example.notecontracts.NoteReply
import com.example.notecontracts.NoteState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer

class NoteKafkaConfigTest {

    @Test
    fun `note reply round-trips through kafka json serializers`() {
        val objectMapper = Jackson2ObjectMapperBuilder.json().build<com.fasterxml.jackson.databind.ObjectMapper>()
        val serializer = JsonSerializer<NoteReply>(objectMapper.copy()).apply {
            setAddTypeInfo(false)
        }
        val deserializer = JsonDeserializer(NoteReply::class.java, objectMapper.copy(), false).apply {
            addTrustedPackages("*")
            setUseTypeHeaders(false)
        }
        val reply = NoteReply(
            correlationId = "corr-2",
            operation = NoteOperation.GET_BY_ID,
            noteId = 11L,
            tweetId = 7L,
            success = true,
            httpStatus = 200,
            note = NotePayload(
                id = 11L,
                tweetId = 7L,
                country = "BY",
                content = "reply",
                state = NoteState.APPROVE
            )
        )

        val payload = serializer.serialize(NoteKafkaTopics.OUT_TOPIC, reply)
        val restored = deserializer.deserialize(NoteKafkaTopics.OUT_TOPIC, payload)

        assertEquals(reply, restored)
    }
}
