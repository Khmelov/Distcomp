package com.distcomp.service

import com.distcomp.dto.kafka.KafkaNoticeResponse
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

@Service
class KafkaResponseRegistry {
    private val pending = ConcurrentHashMap<String, CompletableFuture<KafkaNoticeResponse>>()

    fun register(id: String): CompletableFuture<KafkaNoticeResponse> {
        val future = CompletableFuture<KafkaNoticeResponse>()
        pending[id] = future
        return future
    }

    fun complete(response: KafkaNoticeResponse) {
        pending.remove(response.id)?.complete(response)
    }
}