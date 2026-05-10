package com.distcomp.service

import com.distcomp.dto.kafka.KafkaNoticeResponse
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class NoticeConsumer(
    private val registry: KafkaResponseRegistry
) {
    @KafkaListener(
        topics = ["\${kafka.topics.out}"],
        groupId = "publisher-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun listen(response: KafkaNoticeResponse) {
        registry.complete(response)
    }
}