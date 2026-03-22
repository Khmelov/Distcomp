package com.distcomp.client

import com.distcomp.dto.notice.NoticeRequestTo
import com.distcomp.dto.notice.NoticeResponseTo
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class DiscussionServiceClient(
    private val noticeRestClient: RestClient,
) {
    fun getAll(): List<NoticeResponseTo> =
        noticeRestClient.get()
            .uri("/api/v1.0/notices")
            .retrieve()
            .body(object : ParameterizedTypeReference<List<NoticeResponseTo>>() {})
            ?: emptyList()

    fun getById(id: Long): NoticeResponseTo =
        noticeRestClient.get()
            .uri("/api/v1.0/notices/{id}", id)
            .retrieve()
            .body(NoticeResponseTo::class.java)!!

    fun create(request: NoticeRequestTo): NoticeResponseTo =
        noticeRestClient.post()
            .uri("/api/v1.0/notices")
            .body(request)
            .retrieve()
            .body(NoticeResponseTo::class.java)!!

    fun delete(id: Long) =
        noticeRestClient.delete()
            .uri("/api/v1.0/notices/{id}", id)
            .retrieve()
            .toBodilessEntity()
}