package com.example.entitiesapp.kafka

import com.example.entitiesapp.dto.CommentRequestTo
import com.example.entitiesapp.dto.CommentState
import com.example.entitiesapp.service.CommentService
import com.example.entitiesapp.service.ModerationService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Component

@Component
class CommentListener(
    private val service: CommentService,
    private val moderationService: ModerationService,
    private val objectMapper: ObjectMapper
) {

    @KafkaListener(topics = ["InTopic"])
    @SendTo("OutTopic")
    fun handleAll(record: ConsumerRecord<String, String>): String {
        val op = String(record.headers().lastHeader("op")?.value() ?: "NONE".toByteArray())
        val body = record.value()
        println(">>> KAFKA RECEIVE: $op with body: $body")

        return try {
            val result: Any = when (op) {
                "GET_ALL" -> service.getAll()
                "GET_BY_ID" -> service.getById(body.toLong())
                "CREATE" -> {
                    val dto = objectMapper.readValue<CommentRequestTo>(body)
                    service.create(dto.copy(state = moderationService.moderate(dto.content)))
                }
                "UPDATE" -> {
                    val dto = objectMapper.readValue<CommentRequestTo>(body)
                    service.update(dto.id!!, dto)
                }
                "DELETE" -> {
                    service.delete(body.toLong())
                    mapOf("status" to "success")
                }
                "DELETE_BY_STORY" -> {
                    service.deleteByStoryId(body.toLong())
                    mapOf("status" to "success")
                }
                else -> throw RuntimeException("Unknown op: $op")
            }
            objectMapper.writeValueAsString(result)
        } catch (e: Exception) {
            println("!!! ERROR: ${e.message}")
            "ERROR: ${e.message}"
        }
    }
}