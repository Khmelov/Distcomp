package com.example.discussion.service

import com.example.discussion.dto.request.NoteRequestTo
import com.example.discussion.dto.response.NoteResponseTo
import com.example.discussion.exception.BadRequestException
import com.example.discussion.exception.NotFoundException
import com.example.discussion.model.NoteEntity
import com.example.discussion.model.NoteKey
import com.example.discussion.repository.NoteRepository
import com.example.notecontracts.NoteState
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import kotlin.math.abs

@Service
class NoteService(
    private val noteRepository: NoteRepository,
    private val moderationService: NoteModerationService
) {
    fun create(request: NoteRequestTo, requestedId: Long? = null): NoteEntity {
        val tweetId = request.tweetId ?: throw BadRequestException("tweetId is required")
        val content = request.content ?: throw BadRequestException("content is required")
        val id = requestedId ?: generateId()
        return noteRepository.save(
            NoteEntity(
                key = NoteKey(tweetId = tweetId, id = id),
                country = request.country,
                content = content,
                state = moderationService.moderate(content)
            )
        )
    }

    fun getById(id: Long): NoteResponseTo = toResponse(getByIdEntity(id))

    fun getByIdEntity(id: Long): NoteEntity = requireVisible(id)

    fun getAll(page: Int, size: Int, sort: Array<String>): List<NoteResponseTo> =
        getAllEntities(page, size, sort).map(::toResponse)

    fun getAllEntities(page: Int, size: Int, sort: Array<String>): List<NoteEntity> {
        val all = noteRepository.findAll().toList().filter(::isVisible)
        val sorted = sortResults(all, sort)
        val from = (page * size).coerceAtMost(sorted.size)
        val to = (from + size).coerceAtMost(sorted.size)
        return sorted.subList(from, to)
    }

    fun put(id: Long, request: NoteRequestTo): NoteEntity {
        val existing = requireVisible(id)
        val tweetId = request.tweetId ?: throw BadRequestException("tweetId is required")
        val content = request.content ?: throw BadRequestException("content is required")
        if (existing.key.tweetId != tweetId) {
            noteRepository.delete(existing)
        }
        return noteRepository.save(
            NoteEntity(
                key = NoteKey(tweetId = tweetId, id = id),
                country = request.country,
                content = content,
                state = moderationService.moderate(content)
            )
        )
    }

    fun patch(id: Long, request: NoteRequestTo): NoteEntity {
        val existing = requireVisible(id)
        val newTweetId = request.tweetId ?: existing.key.tweetId
        val newContent = request.content ?: existing.content
        val patched = existing.copy(
            key = NoteKey(tweetId = newTweetId, id = id),
            country = request.country ?: existing.country,
            content = newContent,
            state = if (request.content != null) moderationService.moderate(newContent) else existing.state
        )
        if (existing.key.tweetId != newTweetId) {
            noteRepository.delete(existing)
        }
        return noteRepository.save(patched)
    }

    fun delete(id: Long): NoteEntity {
        val existing = requireVisible(id)
        noteRepository.delete(existing)
        return existing
    }

    fun getByTweetId(tweetId: Long): List<NoteResponseTo> =
        getByTweetIdEntities(tweetId).map(::toResponse)

    fun getByTweetIdEntities(tweetId: Long): List<NoteEntity> =
        noteRepository.findByKeyTweetId(tweetId).filter(::isVisible)

    fun toResponse(entity: NoteEntity): NoteResponseTo = NoteResponseTo(
        id = entity.key.id,
        tweetId = entity.key.tweetId,
        country = entity.country,
        content = entity.content
    )

    private fun sortResults(items: List<NoteEntity>, sort: Array<String>): List<NoteEntity> {
        if (sort.isEmpty()) return items
        val normalized = normalizeSort(sort)
        val field = normalized.firstOrNull() ?: "id"
        val direction = if (normalized.size >= 2) Sort.Direction.fromString(normalized[1]) else Sort.Direction.ASC
        val comparator = when (field) {
            "id" -> compareBy<NoteEntity> { it.key.id }
            "tweetId" -> compareBy<NoteEntity> { it.key.tweetId }
            else -> compareBy<NoteEntity> { it.key.id }
        }
        return if (direction.isAscending) items.sortedWith(comparator) else items.sortedWith(comparator.reversed())
    }

    private fun requireVisible(id: Long): NoteEntity =
        noteRepository.findByKeyId(id).firstOrNull()?.takeIf(::isVisible)
            ?: throw NotFoundException("Note with id $id not found")

    private fun isVisible(entity: NoteEntity): Boolean = entity.state == NoteState.APPROVE

    private fun normalizeSort(sort: Array<String>): List<String> =
        if (sort.size == 1 && sort[0].contains(',')) {
            sort[0].split(',').map(String::trim)
        } else {
            sort.toList()
        }

    private fun generateId(): Long = abs(java.util.UUID.randomUUID().mostSignificantBits)
}
