package com.example.distcomp.service

import com.example.distcomp.dto.request.TweetRequestTo
import com.example.distcomp.dto.response.TweetResponseTo
import com.example.distcomp.dto.response.CreatorResponseTo
import com.example.distcomp.dto.response.NoteResponseTo
import com.example.distcomp.dto.response.StickerResponseTo
import com.example.distcomp.exception.ConflictException
import com.example.distcomp.exception.NotFoundException
import com.example.distcomp.mapper.TweetMapper
import com.example.distcomp.repository.CreatorRepository
import com.example.distcomp.repository.NoteRepository
import com.example.distcomp.repository.StickerRepository
import com.example.distcomp.repository.TweetRepository
import com.example.distcomp.mapper.CreatorMapper
import com.example.distcomp.mapper.NoteMapper
import com.example.distcomp.mapper.StickerMapper
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
    private val noteRepository: NoteRepository,
    private val creatorMapper: CreatorMapper,
    private val stickerMapper: StickerMapper,
    private val noteMapper: NoteMapper
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
        return mapper.toResponse(saved)
    }

    fun getById(id: Long): TweetResponseTo {
        val entity = repository.findById(id) ?: throw NotFoundException("Tweet with id $id not found")
        return mapper.toResponse(entity)
    }

    fun getAll(page: Int, size: Int, sort: Array<String>): List<TweetResponseTo> {
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
                // If the title changed or the creator changed, we need to make sure the combination is unique.
                // Wait, if ONLY the content changed, duplicate will be TRUE (because it's the SAME tweet).
                // So we should only throw if it's a DIFFERENT tweet.
                // Since this check is based on the combination, if it's the SAME combination as current, it's fine.
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
        return mapper.toResponse(saved)
    }

    fun getCreatorByTweetId(id: Long): CreatorResponseTo {
        val tweet = repository.findById(id) ?: throw NotFoundException("Tweet with id $id not found")
        val creatorId = tweet.creatorId ?: throw NotFoundException("Creator for tweet $id not found")
        val creator = creatorRepository.findById(creatorId) ?: throw NotFoundException("Creator with id $creatorId not found")
        return creatorMapper.toResponse(creator)
    }

    fun getStickersByTweetId(id: Long): List<StickerResponseTo> {
        val tweet = repository.findById(id) ?: throw NotFoundException("Tweet with id $id not found")
        return tweet.stickers.map { stickerMapper.toResponse(it) }
    }

    fun getNotesByTweetId(id: Long): List<NoteResponseTo> {
        if (!repository.existsById(id)) throw NotFoundException("Tweet with id $id not found")
        return noteRepository.findByTweetId(id).map { noteMapper.toResponse(it) }
    }

    fun delete(id: Long) {
        if (!repository.deleteById(id)) {
            throw NotFoundException("Tweet with id $id not found")
        }
    }
}
