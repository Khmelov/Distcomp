package com.example.distcomp.service

import com.example.distcomp.cache.CacheKeys
import com.example.distcomp.cache.CacheNames
import com.example.distcomp.cache.CacheSupport
import com.example.distcomp.dto.request.StickerRequestTo
import com.example.distcomp.dto.response.StickerResponseTo
import com.example.distcomp.exception.ConflictException
import com.example.distcomp.exception.NotFoundException
import com.example.distcomp.mapper.StickerMapper
import com.example.distcomp.repository.StickerRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class StickerService(
    private val repository: StickerRepository,
    private val mapper: StickerMapper,
    private val cacheSupport: CacheSupport
) {
    fun create(request: StickerRequestTo): StickerResponseTo {
        if (repository.findByName(request.name!!) != null) {
            throw ConflictException("Sticker with name ${request.name} already exists")
        }
        val entity = mapper.toEntity(request)
        val saved = repository.save(entity)
        return mapper.toResponse(saved).also { response ->
            response.id?.let { cacheSupport.put(CacheNames.STICKERS_BY_ID, it, response) }
            cacheSupport.clear(CacheNames.STICKERS_PAGE)
        }
    }

    fun getById(id: Long): StickerResponseTo =
        cacheSupport.getOrPut(CacheNames.STICKERS_BY_ID, id) {
            val entity = repository.findById(id) ?: throw NotFoundException("Sticker with id $id not found")
            mapper.toResponse(entity)
        }

    fun getAll(page: Int, size: Int, sort: Array<String>): List<StickerResponseTo> =
        cacheSupport.getOrPut(CacheNames.STICKERS_PAGE, CacheKeys.page(page, size, sort)) {
            val sortOrder = if (sort.size >= 2) {
                Sort.by(Sort.Direction.fromString(sort[1]), sort[0])
            } else if (sort.isNotEmpty()) {
                Sort.by(sort[0])
            } else {
                Sort.unsorted()
            }
            val pageable = PageRequest.of(page, size, sortOrder)
            repository.findAll(pageable).content.map { mapper.toResponse(it) }
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
        return mapper.toResponse(saved).also { response ->
            cacheSupport.put(CacheNames.STICKERS_BY_ID, id, response)
            cacheSupport.clear(CacheNames.STICKERS_PAGE)
            cacheSupport.clear(CacheNames.TWEETS_BY_ID)
            cacheSupport.clear(CacheNames.TWEETS_PAGE)
            cacheSupport.clear(CacheNames.TWEET_STICKERS)
        }
    }

    fun delete(id: Long) {
        if (!repository.deleteById(id)) {
            throw NotFoundException("Sticker with id $id not found")
        }
        cacheSupport.evict(CacheNames.STICKERS_BY_ID, id)
        cacheSupport.clear(CacheNames.STICKERS_PAGE)
        cacheSupport.clear(CacheNames.TWEETS_BY_ID)
        cacheSupport.clear(CacheNames.TWEETS_PAGE)
        cacheSupport.clear(CacheNames.TWEET_STICKERS)
    }
}
