package com.example.discussion.service

import com.example.discussion.dto.kafka.KafkaNoticeRequest
import com.example.discussion.dto.kafka.KafkaNoticeResponse
import com.example.discussion.dto.notice.NoticeRequestTo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class NoticeKafkaConsumer(
    private val noticeService: NoticeService,
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    @Value("\${kafka.topics.out}") private val outTopic: String
) {
    private val mapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    @KafkaListener(topics = ["\${kafka.topics.in}"], groupId = "discussion-group")
    fun handle(request: KafkaNoticeRequest) {
        val response = try {
            val data: Any = when (request.type) {
                "GET" -> noticeService.readNoticeById(request.id.toLong())
                "GET_ALL" -> noticeService.readAllNotices()
                "POST" -> noticeService.createNotice(toRequestDto(request.payload))
                "PUT" -> noticeService.updateNotice(request.id.toLong(), toRequestDto(request.payload))
                "DELETE" -> {
                    noticeService.removeNoticeById(request.id.toLong())
                    mapOf("deleted" to true)
                }
                else -> throw IllegalArgumentException("Unknown type: ${request.type}")
            }
            KafkaNoticeResponse(id = request.id, status = "OK", data = data)
        } catch (e: Exception) {
            KafkaNoticeResponse(id = request.id, status = "ERROR", error = e.message)
        }

        kafkaTemplate.send(outTopic, request.id, response)
    }

    private fun toRequestDto(payload: Any?): NoticeRequestTo =
        mapper.convertValue(payload, NoticeRequestTo::class.java)
}