package com.example.entitiesapp.service

import com.example.entitiesapp.dto.MarkRequestTo
import com.example.entitiesapp.dto.MarkResponseTo
import com.example.entitiesapp.exception.NotFoundException
import com.example.entitiesapp.model.Mark
import com.example.entitiesapp.repository.MarkRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class MarkService(private val repository: MarkRepository) {

    private fun checkAdmin() {
        val auth = SecurityContextHolder.getContext().authentication
        if (auth == null || auth.name == "anonymousUser") return
        val isAdmin = auth.authorities.any { it.authority == "ROLE_ADMIN" }
        if (!isAdmin) throw AccessDeniedException("Only ADMIN can manage marks")
    }

    private fun toEntity(dto: MarkRequestTo) = Mark(name = dto.name)
    private fun toResponse(entity: Mark) = MarkResponseTo(id = entity.id!!, name = entity.name)

    fun getAll(): List<MarkResponseTo> = repository.findAll().map { toResponse(it) }

    @Cacheable(value = ["marks"], key = "#id")
    fun getById(id: Long): MarkResponseTo = toResponse(repository.findById(id).orElseThrow { NotFoundException("Mark not found", 40402) })

    fun create(dto: MarkRequestTo): MarkResponseTo {
        checkAdmin()
        return toResponse(repository.save(toEntity(dto)))
    }

    @CachePut(value = ["marks"], key = "#id")
    fun update(id: Long, dto: MarkRequestTo): MarkResponseTo {
        checkAdmin()
        val entity = toEntity(dto).apply { this.id = id }
        return toResponse(repository.save(entity))
    }

    @CacheEvict(value = ["marks"], key = "#id")
    fun delete(id: Long) {
        checkAdmin()
        repository.deleteById(id)
    }
}