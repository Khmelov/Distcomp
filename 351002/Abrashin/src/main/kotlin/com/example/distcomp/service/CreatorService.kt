package com.example.distcomp.service

import com.example.distcomp.dto.request.CreatorRequestTo
import com.example.distcomp.dto.response.CreatorResponseTo
import com.example.distcomp.exception.ConflictException
import com.example.distcomp.exception.NotFoundException
import com.example.distcomp.mapper.CreatorMapper
import com.example.distcomp.repository.CreatorRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class CreatorService(
    private val repository: CreatorRepository,
    private val mapper: CreatorMapper
) {
    fun create(request: CreatorRequestTo): CreatorResponseTo {
        if (repository.findByLogin(request.login!!) != null) {
            throw ConflictException("Creator with login ${request.login} already exists")
        }
        val entity = mapper.toEntity(request)
        val saved = repository.save(entity)
        return mapper.toResponse(saved)
    }

    fun getById(id: Long): CreatorResponseTo {
        val entity = repository.findById(id) ?: throw NotFoundException("Creator with id $id not found")
        return mapper.toResponse(entity)
    }

    fun getAll(page: Int, size: Int, sort: Array<String>): List<CreatorResponseTo> {
        val sortOrder = if (sort.size >= 2) {
            Sort.by(Sort.Direction.fromString(sort[1]), sort[0])
        } else if (sort.isNotEmpty()) {
            Sort.by(sort[0])
        } else {
            Sort.unsorted()
        }
        val pageable = PageRequest.of(page, size, sortOrder)
        return repository.findAll(pageable).content.map { mapper.toResponse(it) }
    }

    fun patch(id: Long, request: CreatorRequestTo): CreatorResponseTo {
        val existing = repository.findById(id) ?: throw NotFoundException("Creator with id $id not found")
        
        request.login?.let {
            val other = repository.findByLogin(it)
            if (other != null && other.id != id) {
                throw ConflictException("Creator with login $it already exists")
            }
            existing.login = it
        }
        request.password?.let { existing.password = it }
        request.firstname?.let { existing.firstname = it }
        request.lastname?.let { existing.lastname = it }
        
        val saved = repository.save(existing)
        return mapper.toResponse(saved)
    }

    fun delete(id: Long) {
        if (!repository.deleteById(id)) {
            throw NotFoundException("Creator with id $id not found")
        }
    }
}

sealed class Hello{
    class World:Hello()
}