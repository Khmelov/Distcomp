package com.distcomp.service

import com.distcomp.dto.kafka.KafkaNoticeRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class NoticeProducer(
    private val kafkaTemplate: KafkaTemplate<String, KafkaNoticeRequest>,
    @Value("\${kafka.topics.in}") private val inTopic: String
) {
    fun send(request: KafkaNoticeRequest) {
        kafkaTemplate.send(inTopic, request.id, request)
    }
}
