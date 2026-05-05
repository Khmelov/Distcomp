package com.example.entitiesapp.service

import com.example.entitiesapp.dto.CommentRequestTo
import com.example.entitiesapp.dto.CommentResponseTo
import com.example.entitiesapp.exception.NotFoundException
import com.example.entitiesapp.model.Comment
import com.example.entitiesapp.repository.CommentRepository
import org.springframework.stereotype.Service
import java.util.*
import kotlin.random.Random

@Service
class CommentService(private val repository: CommentRepository) {

    private fun toResponse(entity: Comment) = CommentResponseTo(
        id = entity.id,
        content = entity.content,
        storyId = entity.storyId,
        country = entity.country,
        state = entity.state
    )

    fun getAll(): List<CommentResponseTo> = repository.findAll().map { toResponse(it) }

    fun getById(id: Long): CommentResponseTo =
        repository.findByCommentId(id)
            .map { toResponse(it) }
            .orElseThrow { NotFoundException("Comment $id not found", 40404) }

    fun create(dto: CommentRequestTo): CommentResponseTo {
        val entity = Comment(
            id = dto.id ?: Math.abs(Random.nextLong(1000000)),
            storyId = dto.storyId,
            content = dto.content,
            country = dto.country ?: "Unknown",
            state = dto.state
        )
        val saved = repository.save(entity)
        return toResponse(saved)
    }

    fun update(id: Long, dto: CommentRequestTo): CommentResponseTo {
        val oldEntity = repository.findByCommentId(id)
            .orElseThrow { NotFoundException("Comment $id not found", 40404) }

        repository.delete(oldEntity)

        val newEntity = Comment(
            id = id,
            storyId = dto.storyId,
            content = dto.content,
            country = dto.country ?: oldEntity.country
        )
        return toResponse(repository.save(newEntity))
    }

    fun delete(id: Long) {
        val entity = repository.findByCommentId(id)
            .orElseThrow { NotFoundException("Comment $id not found", 40404) }
        repository.delete(entity)
    }

    fun deleteByStoryId(storyId: Long) {
        repository.deleteAllByStoryId(storyId)
    }
}