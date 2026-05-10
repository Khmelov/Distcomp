package com.example.distcomp.service

import com.example.distcomp.dto.request.StickerRequestTo
import com.example.distcomp.dto.response.StickerResponseTo
import com.example.distcomp.exception.ConflictException
import com.example.distcomp.exception.NotFoundException
import com.example.distcomp.mapper.StickerMapper
import com.example.distcomp.repository.StickerRepository
import org.springframework.stereotype.Service

@Service
class StickerService(
    private val repository: StickerRepository,
    private val mapper: StickerMapper
) {
    fun create(request: StickerRequestTo): StickerResponseTo {
        if (repository.findByName(request.name!!) != null) {
            throw ConflictException("Sticker with name ${request.name} already exists")
        }
        val entity = mapper.toEntity(request)
        val saved = repository.save(entity)
        return mapper.toResponse(saved)
    }

    fun getById(id: Long): StickerResponseTo {
        val entity = repository.findById(id) ?: throw NotFoundException("Sticker with id $id not found")
        return mapper.toResponse(entity)
    }

    fun getAll(): List<StickerResponseTo> {
        return repository.findAll().map { mapper.toResponse(it) }
    }

    fun patch(id: Long, request: StickerRequestTo): StickerResponseTo {
        val existing = repository.findById(id) ?: throw NotFoundException("Sticker with id $id not found")
        
        request.name?.let {
            val other = repository.findByName(it)
            if (other != null && other.id != id) {
                throw ConflictException("Sticker with name $it already exists")
            }
            existing.name = it
        }
        
        val saved = repository.save(existing)
        return mapper.toResponse(saved)
    }

    fun delete(id: Long) {
        if (!repository.deleteById(id)) {
            throw NotFoundException("Sticker with id $id not found")
        }
    }
}
