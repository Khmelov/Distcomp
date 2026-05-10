package com.example.distcomp.service

import com.example.distcomp.dto.request.TweetRequestTo
import com.example.distcomp.dto.response.TweetResponseTo
import com.example.distcomp.dto.response.CreatorResponseTo
import com.example.distcomp.dto.response.NoteResponseTo
import com.example.distcomp.dto.response.StickerResponseTo
import com.example.distcomp.exception.ConflictException
import com.example.distcomp.exception.NotFoundException
import com.example.distcomp.mapper.TweetMapper
import com.example.distcomp.repository.TweetRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TweetService(
    private val repository: TweetRepository,
    private val mapper: TweetMapper,
    private val creatorService: CreatorService,
    private val stickerService: StickerService,
    private val noteService: NoteService
) {
    fun create(request: TweetRequestTo): TweetResponseTo {
        request.creatorId?.let {
            creatorService.getById(it)
        }
        if (repository.findByTitle(request.title!!) != null) {
            throw ConflictException("Tweet with title ${request.title} already exists")
        }
        val entity = mapper.toEntity(request)
        val now = LocalDateTime.now()
        entity.created = now
        entity.modified = now
        val saved = repository.save(entity)
        return mapper.toResponse(saved)
    }

    fun getById(id: Long): TweetResponseTo {
        val entity = repository.findById(id) ?: throw NotFoundException("Tweet with id $id not found")
        return mapper.toResponse(entity)
    }

    fun getAll(): List<TweetResponseTo> {
        return repository.findAll().map { mapper.toResponse(it) }
    }

    fun patch(id: Long, request: TweetRequestTo): TweetResponseTo {
        val existing = repository.findById(id) ?: throw NotFoundException("Tweet with id $id not found")
        
        request.creatorId?.let {
            creatorService.getById(it)
            existing.creatorId = it
        }
        request.title?.let {
            val other = repository.findByTitle(it)
            if (other != null && other.id != id) {
                throw ConflictException("Tweet with title $it already exists")
            }
            existing.title = it
        }
        request.content?.let { existing.content = it }
        
        existing.modified = LocalDateTime.now()
        val saved = repository.save(existing)
        return mapper.toResponse(saved)
    }

    fun getCreatorByTweetId(id: Long): CreatorResponseTo {
        val tweet = repository.findById(id) ?: throw NotFoundException("Tweet with id $id not found")
        val creatorId = tweet.creatorId ?: throw NotFoundException("Creator for tweet $id not found")
        return creatorService.getById(creatorId)
    }

    fun getStickersByTweetId(id: Long): List<StickerResponseTo> {
        val tweet = repository.findById(id) ?: throw NotFoundException("Tweet with id $id not found")
        return tweet.stickerIds.map { stickerId ->
            stickerService.getById(stickerId)
        }
    }

    fun getNotesByTweetId(id: Long): List<NoteResponseTo> {
        val tweet = repository.findById(id) ?: throw NotFoundException("Tweet with id $id not found")
        return noteService.getNotesByTweetId(tweet.id!!)
    }

    fun delete(id: Long) {
        if (!repository.deleteById(id)) {
            throw NotFoundException("Tweet with id $id not found")
        }
    }
}
