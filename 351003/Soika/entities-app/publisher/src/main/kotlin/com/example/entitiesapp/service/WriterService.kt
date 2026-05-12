package com.example.entitiesapp.service

import com.example.entitiesapp.dto.Role
import com.example.entitiesapp.dto.WriterRequestTo
import com.example.entitiesapp.dto.WriterResponseTo
import com.example.entitiesapp.exception.NotFoundException
import com.example.entitiesapp.model.Writer
import com.example.entitiesapp.repository.StoryRepository
import com.example.entitiesapp.repository.WriterRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class WriterService(
    private val repository: WriterRepository,
    private val storyRepository: StoryRepository,
    private val commentService: CommentService,
    private val passwordEncoder: PasswordEncoder
) {

    private fun checkOwnership(writerId: Long) {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || auth.name == "anonymousUser") return

        val isAdmin = auth.authorities.any { it.authority == "ROLE_ADMIN" }
        val currentWriter = repository.findByLogin(auth.name)

        if (!isAdmin && currentWriter?.id != writerId) {
            throw AccessDeniedException("You can only manage your own profile")
        }
    }

    private fun toEntity(dto: WriterRequestTo) = Writer(
        login = dto.login,
        password = passwordEncoder.encode(dto.password),
        firstname = dto.firstname,
        lastname = dto.lastname,
        role = dto.role ?: Role.CUSTOMER
    )

    private fun toResponse(entity: Writer) = WriterResponseTo(
        id = entity.id!!,
        login = entity.login,
        firstname = entity.firstname,
        lastname = entity.lastname,
        role = entity.role
    )

    fun getAll(): List<WriterResponseTo> = repository.findAll().map { toResponse(it) }

    @Cacheable(value = ["writers"], key = "#id")
    fun getById(id: Long): WriterResponseTo {
        return toResponse(repository.findById(id).orElseThrow { NotFoundException("Writer $id not found", 40401) })
    }

    fun create(dto: WriterRequestTo): WriterResponseTo = toResponse(repository.save(toEntity(dto)))

    @CachePut(value = ["writers"], key = "#id")
    fun update(id: Long, dto: WriterRequestTo): WriterResponseTo {
        checkOwnership(id)
        if (!repository.existsById(id)) throw NotFoundException("Writer $id not found", 40401)
        val entity = toEntity(dto).apply { this.id = id }
        return toResponse(repository.save(entity))
    }

    @CacheEvict(value = ["writers"], key = "#id")
    fun delete(id: Long) {
        checkOwnership(id)
        if (!repository.existsById(id)) throw NotFoundException("Writer $id not found", 40401)
        val stories = storyRepository.findAllByWriterId(id)
        stories.forEach { story ->
            commentService.deleteByStory(story.id!!)
            storyRepository.deleteById(story.id!!)
        }
        repository.deleteById(id)
    }
}