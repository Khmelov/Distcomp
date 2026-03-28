package com.example.entitiesapp.service

import com.example.entitiesapp.dto.CommentRequestTo
import com.example.entitiesapp.dto.CommentResponseTo
import com.example.entitiesapp.exception.NotFoundException
import com.example.entitiesapp.exception.ValidationException
import com.example.entitiesapp.model.Comment
import com.example.entitiesapp.repository.CommentRepository
import com.example.entitiesapp.repository.StoryRepository
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val storyRepository: StoryRepository
) {

    private fun toEntity(dto: CommentRequestTo) =
        Comment(content = dto.content, storyId = dto.storyId)

    private fun toResponse(entity: Comment) =
        CommentResponseTo(id = entity.id!!, content = entity.content, storyId = entity.storyId)

    fun getAll(): List<CommentResponseTo> =
        commentRepository.findAll().map { toResponse(it) }

    fun getById(id: Long): CommentResponseTo =
        toResponse(
            commentRepository.findById(id)
                ?: throw NotFoundException("Comment with id=$id not found", 40404)
        )

    fun create(dto: CommentRequestTo): CommentResponseTo {
        if (storyRepository.findById(dto.storyId) == null)
            throw ValidationException("Story with id=${dto.storyId} does not exist", 40003)
        return toResponse(commentRepository.save(toEntity(dto)))
    }

    fun update(id: Long, dto: CommentRequestTo): CommentResponseTo {
        if (commentRepository.findById(id) == null)
            throw NotFoundException("Comment with id=$id not found", 40404)
        if (storyRepository.findById(dto.storyId) == null)
            throw ValidationException("Story with id=${dto.storyId} does not exist", 40003)
        return toResponse(commentRepository.update(id, toEntity(dto)))
    }

    fun delete(id: Long) {
        if (commentRepository.findById(id) == null)
            throw NotFoundException("Comment with id=$id not found", 40404)
        commentRepository.delete(id)
    }
}