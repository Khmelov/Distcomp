package com.example.entitiesapp.service

import com.example.entitiesapp.dto.StoryRequestTo
import com.example.entitiesapp.dto.StoryResponseTo
import com.example.entitiesapp.exception.NotFoundException
import com.example.entitiesapp.exception.ValidationException
import com.example.entitiesapp.model.Story
import com.example.entitiesapp.repository.MarkRepository
import com.example.entitiesapp.repository.StoryRepository
import com.example.entitiesapp.repository.WriterRepository
import org.springframework.stereotype.Service

@Service
class StoryService(
    private val storyRepository: StoryRepository,
    private val writerRepository: WriterRepository,
    private val markRepository: MarkRepository
) {

    private fun toEntity(dto: StoryRequestTo) =
        Story(
            title = dto.title,
            content = dto.content,
            writerId = dto.writerId,
            markIds = dto.markIds.toMutableSet()
        )

    private fun toResponse(entity: Story) =
        StoryResponseTo(
            id = entity.id!!,
            title = entity.title,
            content = entity.content,
            writerId = entity.writerId,
            markIds = entity.markIds
        )

    fun getAll(): List<StoryResponseTo> =
        storyRepository.findAll().map { toResponse(it) }

    fun getById(id: Long): StoryResponseTo =
        toResponse(
            storyRepository.findById(id)
                ?: throw NotFoundException("Story with id=$id not found", 40403)
        )

    fun create(dto: StoryRequestTo): StoryResponseTo {
        if (writerRepository.findById(dto.writerId) == null)
            throw ValidationException("Writer does not exist", 40001)

        dto.markIds.forEach {
            if (markRepository.findById(it) == null)
                throw ValidationException("Mark id=$it does not exist", 40002)
        }

        return toResponse(storyRepository.save(toEntity(dto)))
    }

    fun update(id: Long, dto: StoryRequestTo): StoryResponseTo {
        val story = storyRepository.findById(id)
            ?: throw NotFoundException("Story with id=$id not found", 40403)

        if (dto.title.isBlank() || dto.title.length < 3)
            throw ValidationException("Title is too short", 40003)
        if (dto.content.isBlank() || dto.content.length < 3)
            throw ValidationException("Content is too short", 40004)

        if (writerRepository.findById(dto.writerId) == null)
            throw ValidationException("Writer does not exist", 40001)

        dto.markIds.forEach {
            if (markRepository.findById(it) == null)
                throw ValidationException("Mark id=$it does not exist", 40002)
        }

        return toResponse(storyRepository.update(id, toEntity(dto)))
    }

    fun delete(id: Long) {
        if (storyRepository.findById(id) == null)
            throw NotFoundException("Story with id=$id not found", 40403)
        storyRepository.delete(id)
    }
}