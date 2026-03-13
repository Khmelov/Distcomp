package com.example.entitiesapp.service

import com.example.entitiesapp.dto.MarkRequestTo
import com.example.entitiesapp.dto.MarkResponseTo
import com.example.entitiesapp.exception.NotFoundException
import com.example.entitiesapp.model.Mark
import com.example.entitiesapp.repository.MarkRepository
import org.springframework.stereotype.Service

@Service
class MarkService(
    private val repository: MarkRepository
) {

    private fun toEntity(dto: MarkRequestTo) = Mark(name = dto.name)
    private fun toResponse(entity: Mark) = MarkResponseTo(id = entity.id!!, name = entity.name)

    fun getAll(): List<MarkResponseTo> = repository.findAll().map { toResponse(it) }

    fun getById(id: Long): MarkResponseTo =
        toResponse(
            repository.findById(id)
                ?: throw NotFoundException("Mark with id=$id not found", 40402)
        )

    fun create(dto: MarkRequestTo): MarkResponseTo =
        toResponse(repository.save(toEntity(dto)))

    fun update(id: Long, dto: MarkRequestTo): MarkResponseTo {
        if (repository.findById(id) == null)
            throw NotFoundException("Mark with id=$id not found", 40402)
        return toResponse(repository.update(id, toEntity(dto)))
    }

    fun delete(id: Long) {
        if (repository.findById(id) == null)
            throw NotFoundException("Mark with id=$id not found", 40402)
        repository.delete(id)
    }
}