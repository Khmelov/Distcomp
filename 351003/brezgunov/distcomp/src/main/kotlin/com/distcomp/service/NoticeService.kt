package com.distcomp.service

import com.distcomp.dto.kafka.KafkaNoticeRequest
import com.distcomp.dto.kafka.KafkaNoticeResponse
import com.distcomp.dto.notice.NoticeRequestTo
import com.distcomp.dto.notice.NoticeResponseTo
import com.distcomp.exception.NewsNotFoundException
import com.distcomp.repository.NewsRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.stereotype.Service
import java.util.UUID
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@Service
class NoticeService(
    private val producer: NoticeProducer,
    private val registry: KafkaResponseRegistry,
    private val newsRepository: NewsRepository
) {
    private val mapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    fun getById(id: Long): NoticeResponseTo {
        val response = sendAndWait(KafkaNoticeRequest(id = id.toString(), type = "GET"))
        return convertToSingleDto(response.data)
    }

    fun getAll(): List<NoticeResponseTo> {
        val response = sendAndWait(KafkaNoticeRequest(id = "all", type = "GET_ALL"))
        return convertToList(response.data)
    }

    fun createNotice(noticeRequestTo: NoticeRequestTo): NoticeResponseTo {
        if (!newsRepository.existsById(noticeRequestTo.newsId)) {
            throw NewsNotFoundException("News not found")
        }
        val response = sendAndWait(KafkaNoticeRequest(id = UUID.randomUUID().toString(), type = "POST", payload = noticeRequestTo))
        return convertToSingleDto(response.data)
    }

    fun updateNotice(noticeId: Long, noticeRequestTo: NoticeRequestTo): NoticeResponseTo {
        if (!newsRepository.existsById(noticeRequestTo.newsId)) {
            throw NewsNotFoundException("News not found")
        }
        val response = sendAndWait(KafkaNoticeRequest(id = noticeId.toString(), type = "PUT", payload = noticeRequestTo))
        return convertToSingleDto(response.data)
    }

    fun deleteById(id: Long) {
        sendAndWait(KafkaNoticeRequest(id = id.toString(), type = "DELETE"))
    }

    private fun sendAndWait(request: KafkaNoticeRequest): KafkaNoticeResponse {
        val future = registry.register(request.id)
        producer.send(request)
        return try {
            future.get(1, TimeUnit.SECONDS)
        } catch (_: TimeoutException) {
            throw RuntimeException("timeout")
        }
    }

    private fun convertToSingleDto(value: Any?): NoticeResponseTo =
        mapper.convertValue(value, NoticeResponseTo::class.java)

    private fun convertToList(value: Any?): List<NoticeResponseTo> =
        mapper.convertValue(value, object : TypeReference<List<NoticeResponseTo>>() {})
}