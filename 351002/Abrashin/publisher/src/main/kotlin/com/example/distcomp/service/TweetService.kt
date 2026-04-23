package com.example.distcomp.service

import com.example.distcomp.cache.CacheKeys
import com.example.distcomp.cache.CacheNames
import com.example.distcomp.cache.CacheSupport
import com.example.distcomp.dto.request.TweetRequestTo
import com.example.distcomp.dto.response.CreatorResponseTo
import com.example.distcomp.dto.response.NoteResponseTo
import com.example.distcomp.dto.response.StickerResponseTo
import com.example.distcomp.dto.response.TweetResponseTo
import com.example.distcomp.exception.ConflictException
import com.example.distcomp.exception.NotFoundException
import com.example.distcomp.mapper.CreatorMapper
import com.example.distcomp.mapper.StickerMapper
import com.example.distcomp.repository.CreatorRepository
import com.example.distcomp.repository.StickerRepository
import com.example.distcomp.repository.TweetRepository
import com.example.distcomp.mapper.TweetMapper
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class TweetService(
    private val repository: TweetRepository,
    private val mapper: TweetMapper,
    private val creatorRepository: CreatorRepository,
    private val stickerRepository: StickerRepository,
    private val noteService: NoteService,
    private val creatorMapper: CreatorMapper,
    private val stickerMapper: StickerMapper,
    private val cacheSupport: CacheSupport
) {
    @Transactional
    fun create(request: TweetRequestTo): TweetResponseTo {
        val creatorId = request.creatorId ?: throw ConflictException("Creator ID is required")
        if (!creatorRepository.existsById(creatorId)) throw NotFoundException("Creator with id $creatorId not found")

        request.title?.let { title ->
            if (repository.existsByCreatorIdAndTitle(creatorId, title)) {
                throw ConflictException("Tweet with title $title already exists for creator $creatorId")
            }
        }

        val entity = mapper.toEntity(request)
        entity.creatorId = creatorId

        entity.stickers = request.stickers?.map { name ->
            stickerRepository.findByName(name) ?: stickerRepository.save(com.example.distcomp.model.Sticker(name = name))
        } ?: emptyList()

        val now = LocalDateTime.now()
        entity.created = now
        entity.modified = now
        val saved = repository.save(entity)
        return mapper.toResponse(saved).also { response ->
            response.id?.let { cacheSupport.put(CacheNames.TWEETS_BY_ID, it, response) }
            cacheSupport.clear(CacheNames.TWEETS_PAGE)
            cacheSupport.clear(CacheNames.STICKERS_PAGE)
        }
    }

    fun getById(id: Long): TweetResponseTo =
        cacheSupport.getOrPut(CacheNames.TWEETS_BY_ID, id) {
            val entity = repository.findById(id) ?: throw NotFoundException("Tweet with id $id not found")
            mapper.toResponse(entity)
        }

    fun getAll(page: Int, size: Int, sort: Array<String>): List<TweetResponseTo> =
        cacheSupport.getOrPut(CacheNames.TWEETS_PAGE, CacheKeys.page(page, size, sort)) {
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

    @Transactional
    fun patch(id: Long, request: TweetRequestTo): TweetResponseTo {
        val existing = repository.findById(id) ?: throw NotFoundException("Tweet with id $id not found")

        request.creatorId?.let { newCreatorId ->
            if (!creatorRepository.existsById(newCreatorId)) throw NotFoundException("Creator with id $newCreatorId not found")
            existing.creatorId = newCreatorId
        }

        val currentCreatorId = existing.creatorId ?: throw ConflictException("Existing tweet has no creator")
        val newTitle = request.title ?: existing.title
        
        if (newTitle != null) {
            val duplicate = repository.existsByCreatorIdAndTitle(currentCreatorId, newTitle)
            if (duplicate && (newTitle != existing.title || request.creatorId != null)) {
                if (newTitle != existing.title || (request.creatorId != null && request.creatorId != existing.creatorId)) {
                    throw ConflictException("Tweet with title $newTitle already exists for creator $currentCreatorId")
                }
            }
        }

        request.title?.let { existing.title = it }
        request.content?.let { existing.content = it }
        request.stickers?.let { names ->
            existing.stickers = names.map { name ->
                stickerRepository.findByName(name) ?: stickerRepository.save(com.example.distcomp.model.Sticker(name = name))
            }
        }

        existing.modified = LocalDateTime.now()
        val saved = repository.save(existing)
        return mapper.toResponse(saved).also { response ->
            cacheSupport.put(CacheNames.TWEETS_BY_ID, id, response)
            cacheSupport.clear(CacheNames.TWEETS_PAGE)
            cacheSupport.evict(CacheNames.TWEET_CREATORS, id)
            cacheSupport.evict(CacheNames.TWEET_STICKERS, id)
            cacheSupport.clear(CacheNames.STICKERS_PAGE)
        }
    }

    fun getCreatorByTweetId(id: Long): CreatorResponseTo =
        cacheSupport.getOrPut(CacheNames.TWEET_CREATORS, id) {
            val tweet = repository.findById(id) ?: throw NotFoundException("Tweet with id $id not found")
            val creatorId = tweet.creatorId ?: throw NotFoundException("Creator for tweet $id not found")
            val creator = creatorRepository.findById(creatorId)
                ?: throw NotFoundException("Creator with id $creatorId not found")
            creatorMapper.toResponse(creator)
        }

    fun getStickersByTweetId(id: Long): List<StickerResponseTo> =
        cacheSupport.getOrPut(CacheNames.TWEET_STICKERS, id) {
            val tweet = repository.findById(id) ?: throw NotFoundException("Tweet with id $id not found")
            tweet.stickers.map { stickerMapper.toResponse(it) }
        }

    fun getNotesByTweetId(id: Long): List<NoteResponseTo> {
        if (!repository.existsById(id)) throw NotFoundException("Tweet with id $id not found")
        return noteService.getNotesByTweetId(id)
    }

    fun delete(id: Long) {
        if (!repository.deleteById(id)) {
            throw NotFoundException("Tweet with id $id not found")
        }
        cacheSupport.evict(CacheNames.TWEETS_BY_ID, id)
        cacheSupport.clear(CacheNames.TWEETS_PAGE)
        cacheSupport.evict(CacheNames.TWEET_CREATORS, id)
        cacheSupport.evict(CacheNames.TWEET_STICKERS, id)
        cacheSupport.evict(CacheNames.NOTES_BY_TWEET_ID, id)
    }
}
