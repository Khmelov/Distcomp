package com.distcomp.client

import com.distcomp.dto.notice.NoticeRequestTo
import com.distcomp.dto.notice.NoticeResponseTo
import com.distcomp.exception.AbstractException
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class DiscussionServiceClient(
    private val restClient: RestClient,
) {
    private fun RestClient.ResponseSpec.handleErrors(): RestClient.ResponseSpec =
        this.onStatus({ it.is4xxClientError }) { _, response ->
            throw object : AbstractException(HttpStatus.valueOf(response.statusCode.value()), response.statusText) {}
        }.onStatus({ it.is5xxServerError }) { _, response ->
            throw object : AbstractException(HttpStatus.valueOf(response.statusCode.value()), response.statusText) {}
        }

    fun getAll(): List<NoticeResponseTo> =
        restClient.get()
            .uri("/api/v1.0/notices")
            .retrieve()
            .handleErrors()
            .body(object : ParameterizedTypeReference<List<NoticeResponseTo>>() {})
            ?: emptyList()

    fun getById(id: Long): NoticeResponseTo =
        restClient.get()
            .uri("/api/v1.0/notices/{id}", id)
            .retrieve()
            .handleErrors()
            .body(NoticeResponseTo::class.java)!!

    fun create(request: NoticeRequestTo): NoticeResponseTo =
        restClient.post()
            .uri("/api/v1.0/notices")
            .body(request)
            .retrieve()
            .handleErrors()
            .body(NoticeResponseTo::class.java)!!

    fun update(id: Long, request: NoticeRequestTo): NoticeResponseTo =
        restClient.put()
            .uri("/api/v1.0/notices/{id}", id)
            .body(request)
            .retrieve()
            .handleErrors()
            .body(NoticeResponseTo::class.java)!!

    fun delete(id: Long) =
        restClient.delete()
            .uri("/api/v1.0/notices/{id}", id)
            .retrieve()
            .handleErrors()
            .toBodilessEntity()
}