package com.example.entitiesapp.service

import com.example.entitiesapp.dto.CommentRequestTo
import com.example.entitiesapp.dto.CommentResponseTo
import com.example.entitiesapp.dto.CommentState
import com.example.entitiesapp.exception.NotFoundException
import com.example.entitiesapp.repository.StoryRepository
import com.example.entitiesapp.repository.WriterRepository
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class CommentService(
    private val replyingKafkaTemplate: ReplyingKafkaTemplate<String, String, String>,
    private val objectMapper: ObjectMapper,
    private val storyRepository: StoryRepository,
    private val writerRepository: WriterRepository
) {
    private val inTopic = "InTopic"

    private fun checkOwnership(commentId: Long) {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || auth.name == "anonymousUser") return
        val isAdmin = auth.authorities.any { it.authority == "ROLE_ADMIN" }

        val comment = getById(commentId)
        val story = storyRepository.findById(comment.storyId).get()
        val storyOwner = writerRepository.findById(story.writerId).get()

        if (!isAdmin && storyOwner.login != auth.name) {
            throw AccessDeniedException("You can only manage comments for your own stories")
        }
    }

    private fun sendRequest(op: String, value: Any?, key: String? = null): String {
        val jsonValue = if (value is String) value else objectMapper.writeValueAsString(value)
        val record = ProducerRecord<String, String>(inTopic, key, jsonValue)
        record.headers().add("op", op.toByteArray())
        val reply = replyingKafkaTemplate.sendAndReceive(record)
        return reply.get().value()
    }

    fun getAll(): List<CommentResponseTo> = objectMapper.readValue(sendRequest("GET_ALL", "EMPTY"))

    @Cacheable(value = ["comments"], key = "#id")
    fun getById(id: Long): CommentResponseTo = objectMapper.readValue(sendRequest("GET_BY_ID", id.toString()))

    @CachePut(value = ["comments"], key = "#result.id")
    fun create(dto: CommentRequestTo): CommentResponseTo {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth != null && auth.name != "anonymousUser") {
            val story = storyRepository.findById(dto.storyId).orElseThrow { NotFoundException("Story not found", 40403) }
            val storyOwner = writerRepository.findById(story.writerId).get()
            if (!auth.authorities.any { it.authority == "ROLE_ADMIN" } && storyOwner.login != auth.name) {
                throw AccessDeniedException("You can only comment on your own stories")
            }
        }
        val id = Math.abs(Random.nextLong(1000000))
        return objectMapper.readValue(sendRequest("CREATE", dto.copy(id = id, state = CommentState.PENDING), dto.storyId.toString()))
    }

    @CachePut(value = ["comments"], key = "#id")
    fun update(id: Long, dto: CommentRequestTo): CommentResponseTo {
        checkOwnership(id)
        return objectMapper.readValue(sendRequest("UPDATE", dto.copy(id = id), dto.storyId.toString()))
    }

    @CacheEvict(value = ["comments"], key = "#id")
    fun delete(id: Long) {
        checkOwnership(id)
        sendRequest("DELETE", id.toString())
    }

    @CacheEvict(value = ["comments"], allEntries = true)
    fun deleteByStory(storyId: Long) {
        sendRequest("DELETE_BY_STORY", storyId.toString(), storyId.toString())
    }
}