package com.example.distcomp.client

import com.example.distcomp.dto.request.NoteRequestTo
import com.example.distcomp.dto.response.NoteResponseTo
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.net.InetAddress

@Component
class DiscussionClient(
    @Value("\${discussion.base-url:}") baseUrl: String
) {
    private val restClient = RestClient.builder()
        .baseUrl(resolveBaseUrl(baseUrl))
        .build()

    fun create(request: NoteRequestTo): NoteResponseTo =
        restClient.post()
            .uri("/api/v1.0/notes")
            .body(request)
            .retrieve()
            .body(NoteResponseTo::class.java)!!

    fun getById(id: Long): NoteResponseTo =
        restClient.get()
            .uri("/api/v1.0/notes/{id}", id)
            .retrieve()
            .body(NoteResponseTo::class.java)!!

    fun getAll(page: Int, size: Int, sort: Array<String>): List<NoteResponseTo> =
        restClient.get()
            .uri { builder ->
                builder.path("/api/v1.0/notes")
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .queryParam("sort", *sort)
                    .build()
            }
            .retrieve()
            .body(object : ParameterizedTypeReference<List<NoteResponseTo>>() {}) ?: emptyList()

    fun update(id: Long, request: NoteRequestTo): NoteResponseTo =
        restClient.put()
            .uri("/api/v1.0/notes/{id}", id)
            .body(request)
            .retrieve()
            .body(NoteResponseTo::class.java)!!

    fun patch(id: Long, request: NoteRequestTo): NoteResponseTo =
        restClient.patch()
            .uri("/api/v1.0/notes/{id}", id)
            .body(request)
            .retrieve()
            .body(NoteResponseTo::class.java)!!

    fun delete(id: Long) {
        restClient.delete()
            .uri("/api/v1.0/notes/{id}", id)
            .retrieve()
            .toBodilessEntity()
    }

    fun getByTweetId(tweetId: Long): List<NoteResponseTo> =
        restClient.get()
            .uri("/api/v1.0/tweets/{id}/notes", tweetId)
            .retrieve()
            .body(object : ParameterizedTypeReference<List<NoteResponseTo>>() {}) ?: emptyList()

    private fun resolveBaseUrl(configuredBaseUrl: String): String {
        if (configuredBaseUrl.isNotBlank()) {
            return configuredBaseUrl
        }
        return if (canResolve("discussion")) {
            "http://discussion:24130"
        } else {
            "http://localhost:24130"
        }
    }

    private fun canResolve(host: String): Boolean = try {
        InetAddress.getByName(host)
        true
    } catch (_: Exception) {
        false
    }
}
