package com.example.distcomp.service

import com.example.distcomp.dto.request.NoteRequestTo
import com.example.distcomp.dto.response.NoteResponseTo
import com.example.distcomp.exception.NotFoundException
import com.example.distcomp.mapper.NoteMapper
import com.example.distcomp.repository.NoteRepository
import com.example.distcomp.repository.TweetRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NoteService(
    private val repository: NoteRepository,
    private val tweetRepository: TweetRepository,
    private val mapper: NoteMapper
) {
    @Transactional
    fun create(request: NoteRequestTo): NoteResponseTo {
        request.tweetId?.let {
            if (!tweetRepository.existsById(it)) throw NotFoundException("Tweet with id $it not found")
        }
        val entity = mapper.toEntity(request)
        entity.tweetId = request.tweetId
        val saved = repository.save(entity)
        return mapper.toResponse(saved)
    }

    fun getById(id: Long): NoteResponseTo {
        val entity = repository.findById(id) ?: throw NotFoundException("Note with id $id not found")
        return mapper.toResponse(entity)
    }

    fun getAll(page: Int, size: Int, sort: Array<String>): List<NoteResponseTo> {
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
    fun patch(id: Long, request: NoteRequestTo): NoteResponseTo {
        val existing = repository.findById(id) ?: throw NotFoundException("Note with id $id not found")
        
        request.tweetId?.let {
            if (!tweetRepository.existsById(it)) throw NotFoundException("Tweet with id $it not found")
            existing.tweetId = it
        }
        request.content?.let { existing.content = it }
        
        val saved = repository.save(existing)
        return mapper.toResponse(saved)
    }

    fun delete(id: Long) {
        if (!repository.deleteById(id)) {
            throw NotFoundException("Note with id $id not found")
        }
    }

    fun getNotesByTweetId(tweetId: Long): List<NoteResponseTo> {
        return repository.findByTweetId(tweetId).map { mapper.toResponse(it) }
    }
}