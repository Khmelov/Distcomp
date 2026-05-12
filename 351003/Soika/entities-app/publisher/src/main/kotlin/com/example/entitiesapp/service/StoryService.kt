package com.example.entitiesapp.service

import com.example.entitiesapp.dto.StoryRequestTo
import com.example.entitiesapp.dto.StoryResponseTo
import com.example.entitiesapp.exception.NotFoundException
import com.example.entitiesapp.exception.ValidationException
import com.example.entitiesapp.model.Mark
import com.example.entitiesapp.model.Story
import com.example.entitiesapp.repository.MarkRepository
import com.example.entitiesapp.repository.StoryRepository
import com.example.entitiesapp.repository.WriterRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class StoryService(
    private val storyRepository: StoryRepository,
    private val writerRepository: WriterRepository,
    private val markRepository: MarkRepository,
    private val commentService: CommentService
) {

    private fun checkOwnership(storyId: Long) {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || auth.name == "anonymousUser") return

        val isAdmin = auth.authorities.any { it.authority == "ROLE_ADMIN" }
        val story = storyRepository.findById(storyId).orElseThrow { NotFoundException("Story not found", 40403) }
        val owner = writerRepository.findById(story.writerId).get()

        if (!isAdmin && owner.login != auth.name) {
            throw AccessDeniedException("You can only manage your own stories")
        }
    }

    private fun toEntity(dto: StoryRequestTo): Story {
        val marksByIds = dto.markIds.map { markRepository.findById(it).orElseThrow { ValidationException("Mark missing", 40002) } }
        val marksByName = dto.marks.map { name -> markRepository.findByName(name) ?: markRepository.save(Mark(name = name)) }
        return Story(writerId = dto.writerId, title = dto.title, content = dto.content, marks = (marksByIds + marksByName).toMutableSet())
    }

    private fun toResponse(entity: Story) = StoryResponseTo(
        id = entity.id!!, title = entity.title, content = entity.content, writerId = entity.writerId, markIds = entity.marks.map { it.id!! }.toSet()
    )

    fun getAll(): List<StoryResponseTo> = storyRepository.findAll().map { toResponse(it) }

    @Cacheable(value = ["stories"], key = "#id")
    fun getById(id: Long): StoryResponseTo = toResponse(storyRepository.findById(id).orElseThrow { NotFoundException("Story not found", 40403) })

    fun create(dto: StoryRequestTo): StoryResponseTo {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth != null && auth.name != "anonymousUser") {
            val currentWriter = writerRepository.findByLogin(auth.name)
            if (dto.writerId != currentWriter?.id && !auth.authorities.any { it.authority == "ROLE_ADMIN" }) {
                throw AccessDeniedException("Cannot create story for another writer")
            }
        }
        val entity = toEntity(dto).apply { created = LocalDateTime.now(); modified = LocalDateTime.now() }
        return toResponse(storyRepository.save(entity))
    }

    @CachePut(value = ["stories"], key = "#id")
    fun update(id: Long, dto: StoryRequestTo): StoryResponseTo {
        checkOwnership(id)
        val entity = toEntity(dto).apply { this.id = id; modified = LocalDateTime.now() }
        return toResponse(storyRepository.save(entity))
    }

    @CacheEvict(value = ["stories"], key = "#id")
    fun delete(id: Long) {
        checkOwnership(id)
        commentService.deleteByStory(id)
        storyRepository.deleteById(id)
    }
}