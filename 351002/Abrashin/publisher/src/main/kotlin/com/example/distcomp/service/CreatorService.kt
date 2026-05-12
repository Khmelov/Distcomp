package com.example.distcomp.service

import com.example.distcomp.cache.CacheKeys
import com.example.distcomp.cache.CacheNames
import com.example.distcomp.cache.CacheSupport
import com.example.distcomp.dto.request.CreatorRequestTo
import com.example.distcomp.dto.response.CreatorResponseTo
import com.example.distcomp.exception.ConflictException
import com.example.distcomp.exception.NotFoundException
import com.example.distcomp.mapper.CreatorMapper
import com.example.distcomp.model.CreatorRole
import com.example.distcomp.repository.CreatorRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CreatorService(
    private val repository: CreatorRepository,
    private val mapper: CreatorMapper,
    private val cacheSupport: CacheSupport,
    private val passwordEncoder: PasswordEncoder
) {
    fun create(request: CreatorRequestTo): CreatorResponseTo {
        if (repository.findByLogin(request.login!!) != null) {
            throw ConflictException("Creator with login ${request.login} already exists")
        }
        val entity = mapper.toEntity(request)
        entity.password = passwordEncoder.encode(request.password ?: throw ConflictException("Password is required"))
        entity.role = request.role ?: CreatorRole.CUSTOMER
        val saved = repository.save(entity)
        return mapper.toResponse(saved).also { response ->
            response.id?.let { cacheSupport.put(CacheNames.CREATORS_BY_ID, it, response) }
            cacheSupport.clear(CacheNames.CREATORS_PAGE)
        }
    }

    fun getById(id: Long): CreatorResponseTo =
        cacheSupport.getOrPut(CacheNames.CREATORS_BY_ID, id) {
            val entity = repository.findById(id) ?: throw NotFoundException("Creator with id $id not found")
            mapper.toResponse(entity)
        }

    fun getAll(page: Int, size: Int, sort: Array<String>): List<CreatorResponseTo> =
        cacheSupport.getOrPut(CacheNames.CREATORS_PAGE, CacheKeys.page(page, size, sort)) {
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

    fun patch(id: Long, request: CreatorRequestTo): CreatorResponseTo {
        val existing = repository.findById(id) ?: throw NotFoundException("Creator with id $id not found")

        request.login?.let {
            val other = repository.findByLogin(it)
            if (other != null && other.id != id) {
                throw ConflictException("Creator with login $it already exists")
            }
            existing.login = it
        }
        request.password?.let { existing.password = passwordEncoder.encode(it) }
        request.firstname?.let { existing.firstname = it }
        request.lastname?.let { existing.lastname = it }
        request.role?.let { existing.role = it }

        val saved = repository.save(existing)
        return mapper.toResponse(saved).also { response ->
            cacheSupport.put(CacheNames.CREATORS_BY_ID, id, response)
            cacheSupport.clear(CacheNames.CREATORS_PAGE)
            cacheSupport.clear(CacheNames.TWEET_CREATORS)
        }
    }

    fun delete(id: Long) {
        if (!repository.deleteById(id)) {
            throw NotFoundException("Creator with id $id not found")
        }
        cacheSupport.evict(CacheNames.CREATORS_BY_ID, id)
        cacheSupport.clear(CacheNames.CREATORS_PAGE)
        cacheSupport.clear(CacheNames.TWEETS_BY_ID)
        cacheSupport.clear(CacheNames.TWEETS_PAGE)
        cacheSupport.clear(CacheNames.TWEET_CREATORS)
        cacheSupport.clear(CacheNames.TWEET_STICKERS)
    }
}
