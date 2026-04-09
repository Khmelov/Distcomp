package com.example.discussion.service

import com.example.discussion.dto.request.NoteRequestTo
import com.example.discussion.dto.response.NoteResponseTo
import com.example.discussion.exception.BadRequestException
import com.example.discussion.exception.NotFoundException
import com.example.discussion.model.NoteEntity
import com.example.discussion.model.NoteKey
import com.example.discussion.repository.NoteRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.math.abs

@Service
class NoteService(
    private val noteRepository: NoteRepository
) {
    fun create(request: NoteRequestTo): NoteResponseTo {
        val tweetId = request.tweetId ?: throw BadRequestException("tweetId is required")
        val content = request.content ?: throw BadRequestException("content is required")
        val id = generateId()
        val saved = noteRepository.save(
            NoteEntity(
                key = NoteKey(tweetId = tweetId, id = id),
                country = request.country,
                content = content
            )
        )
        return toResponse(saved)
    }

    fun getById(id: Long): NoteResponseTo =
        toResponse(
            noteRepository.findByKeyId(id).firstOrNull()
                ?: throw NotFoundException("Note with id $id not found")
        )

    fun getAll(page: Int, size: Int, sort: Array<String>): List<NoteResponseTo> {
        val all = noteRepository.findAll().toList().map(::toResponse)
        val sorted = sortResults(all, sort)
        val from = (page * size).coerceAtMost(sorted.size)
        val to = (from + size).coerceAtMost(sorted.size)
        return sorted.subList(from, to)
    }

    fun put(id: Long, request: NoteRequestTo): NoteResponseTo {
        val existing = noteRepository.findByKeyId(id).firstOrNull()
            ?: throw NotFoundException("Note with id $id not found")
        val tweetId = request.tweetId ?: throw BadRequestException("tweetId is required")
        val content = request.content ?: throw BadRequestException("content is required")
        if (existing.key.tweetId != tweetId) {
            noteRepository.delete(existing)
        }
        val saved = noteRepository.save(
            NoteEntity(
                key = NoteKey(tweetId = tweetId, id = id),
                country = request.country,
                content = content
            )
        )
        return toResponse(saved)
    }

    fun patch(id: Long, request: NoteRequestTo): NoteResponseTo {
        val existing = noteRepository.findByKeyId(id).firstOrNull()
            ?: throw NotFoundException("Note with id $id not found")
        val newTweetId = request.tweetId ?: existing.key.tweetId
        val patched = existing.copy(
            key = NoteKey(tweetId = newTweetId, id = id),
            country = request.country ?: existing.country,
            content = request.content ?: existing.content
        )
        if (existing.key.tweetId != newTweetId) {
            noteRepository.delete(existing)
        }
        return toResponse(noteRepository.save(patched))
    }

    fun delete(id: Long) {
        val existing = noteRepository.findByKeyId(id).firstOrNull()
            ?: throw NotFoundException("Note with id $id not found")
        noteRepository.delete(existing)
    }

    fun getByTweetId(tweetId: Long): List<NoteResponseTo> =
        noteRepository.findByKeyTweetId(tweetId).map(::toResponse)

    private fun toResponse(entity: NoteEntity): NoteResponseTo = NoteResponseTo(
        id = entity.key.id,
        tweetId = entity.key.tweetId,
        country = entity.country,
        content = entity.content
    )

    private fun sortResults(items: List<NoteResponseTo>, sort: Array<String>): List<NoteResponseTo> {
        if (sort.isEmpty()) return items
        val field = sort.first()
        val direction = if (sort.size >= 2) Sort.Direction.fromString(sort[1]) else Sort.Direction.ASC
        val comparator = when (field) {
            "id" -> compareBy<NoteResponseTo> { it.id ?: 0L }
            "tweetId" -> compareBy<NoteResponseTo> { it.tweetId ?: 0L }
            else -> compareBy<NoteResponseTo> { it.id ?: 0L }
        }
        return if (direction.isAscending) items.sortedWith(comparator) else items.sortedWith(comparator.reversed())
    }

    private fun generateId(): Long = abs(java.util.UUID.randomUUID().mostSignificantBits)
}
