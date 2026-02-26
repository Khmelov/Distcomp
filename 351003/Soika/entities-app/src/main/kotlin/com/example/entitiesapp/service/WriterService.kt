package com.example.entitiesapp.service

import com.example.entitiesapp.dto.WriterRequestTo
import com.example.entitiesapp.dto.WriterResponseTo
import com.example.entitiesapp.exception.NotFoundException
import com.example.entitiesapp.model.Writer
import com.example.entitiesapp.repository.WriterRepository
import org.springframework.stereotype.Service

@Service
class WriterService(
    private val repository: WriterRepository
) {

    private fun toEntity(dto: WriterRequestTo) =
        Writer(
            login = dto.login,
            password = dto.password,
            firstname = dto.firstname,
            lastname = dto.lastname
        )

    private fun toResponse(entity: Writer) =
        WriterResponseTo(
            id = entity.id!!,
            login = entity.login,
            password = entity.password,
            firstname = entity.firstname,
            lastname = entity.lastname
        )

    fun getAll(): List<WriterResponseTo> =
        repository.findAll().map { toResponse(it) }

    fun getById(id: Long): WriterResponseTo =
        toResponse(
            repository.findById(id)
                ?: throw NotFoundException("Writer with id=$id not found", 40401)
        )

    fun create(dto: WriterRequestTo): WriterResponseTo =
        toResponse(repository.save(toEntity(dto)))

    fun update(id: Long, dto: WriterRequestTo): WriterResponseTo {
        if (repository.findById(id) == null)
            throw NotFoundException("Writer with id=$id not found", 40401)
        return toResponse(repository.update(id, toEntity(dto)))
    }

    fun delete(id: Long) {
        if (repository.findById(id) == null)
            throw NotFoundException("Writer with id=$id not found", 40401)
        repository.delete(id)
    }
}