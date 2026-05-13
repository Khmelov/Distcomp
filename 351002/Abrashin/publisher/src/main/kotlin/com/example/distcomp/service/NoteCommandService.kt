package com.example.distcomp.service

import com.example.distcomp.dto.request.NoteRequestTo
import com.example.distcomp.dto.response.NoteResponseTo
import com.example.distcomp.exception.BadRequestException
import com.example.distcomp.exception.NotFoundException
import com.example.distcomp.exception.RemoteDiscussionException
import com.example.notecontracts.NoteCommand
import com.example.notecontracts.NoteOperation
import com.example.notecontracts.NotePayload
import com.example.notecontracts.NoteReply
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.math.abs

@Service
class NoteCommandService(
    private val kafkaGateway: PublisherNoteGateway,
    private val projectionSyncService: NoteProjectionStore,
    private val tweetRepository: com.example.distcomp.repository.TweetRepository
) {
    fun create(request: NoteRequestTo): NoteResponseTo {
        val tweetId = request.tweetId ?: throw BadRequestException("tweetId is required")
        val content = request.content ?: throw BadRequestException("content is required")
        if (!tweetRepository.existsById(tweetId)) {
            throw NotFoundException("Tweet with id $tweetId not found")
        }

        val noteId = generateId()
        projectionSyncService.markPending(noteId, tweetId)
        kafkaGateway.send(
            tweetId.toString(),
            NoteCommand(
                correlationId = UUID.randomUUID().toString(),
                operation = NoteOperation.CREATE,
                noteId = noteId,
                tweetId = tweetId,
                country = request.country,
                content = content
            )
        )
        return NoteResponseTo(id = noteId, tweetId = tweetId, country = request.country, content = content)
    }

    fun getById(id: Long): NoteResponseTo {
        val route = projectionSyncService.requireRoute(id)
        return requireNote(
            kafkaGateway.sendAndAwait(
                route.tweetId.toString(),
                NoteCommand(
                    correlationId = UUID.randomUUID().toString(),
                    operation = NoteOperation.GET_BY_ID,
                    noteId = id,
                    tweetId = route.tweetId
                )
            )
        )
    }

    fun getAll(page: Int, size: Int, sort: Array<String>): List<NoteResponseTo> =
        requireNotes(
            kafkaGateway.sendAndAwait(
                "notes-all",
                NoteCommand(
                    correlationId = UUID.randomUUID().toString(),
                    operation = NoteOperation.GET_ALL,
                    page = page,
                    size = size,
                    sort = normalizeSort(sort)
                )
            )
        )

    fun put(id: Long, request: NoteRequestTo): NoteResponseTo {
        val targetTweetId = request.tweetId ?: throw BadRequestException("tweetId is required")
        val content = request.content ?: throw BadRequestException("content is required")
        if (!tweetRepository.existsById(targetTweetId)) {
            throw NotFoundException("Tweet with id $targetTweetId not found")
        }
        val route = projectionSyncService.requireRoute(id)
        return requireNote(
            kafkaGateway.sendAndAwait(
                route.tweetId.toString(),
                NoteCommand(
                    correlationId = UUID.randomUUID().toString(),
                    operation = NoteOperation.PUT,
                    noteId = id,
                    tweetId = targetTweetId,
                    country = request.country,
                    content = content
                )
            )
        )
    }

    fun patch(id: Long, request: NoteRequestTo): NoteResponseTo {
        request.tweetId?.let {
            if (!tweetRepository.existsById(it)) {
                throw NotFoundException("Tweet with id $it not found")
            }
        }
        val route = projectionSyncService.requireRoute(id)
        return requireNote(
            kafkaGateway.sendAndAwait(
                route.tweetId.toString(),
                NoteCommand(
                    correlationId = UUID.randomUUID().toString(),
                    operation = NoteOperation.PATCH,
                    noteId = id,
                    tweetId = request.tweetId ?: route.tweetId,
                    country = request.country,
                    content = request.content
                )
            )
        )
    }

    fun delete(id: Long) {
        val route = projectionSyncService.requireRoute(id)
        requireSuccess(
            kafkaGateway.sendAndAwait(
                route.tweetId.toString(),
                NoteCommand(
                    correlationId = UUID.randomUUID().toString(),
                    operation = NoteOperation.DELETE,
                    noteId = id,
                    tweetId = route.tweetId
                )
            )
        )
    }

    fun getByTweetId(tweetId: Long): List<NoteResponseTo> {
        if (!tweetRepository.existsById(tweetId)) {
            throw NotFoundException("Tweet with id $tweetId not found")
        }
        return requireNotes(
            kafkaGateway.sendAndAwait(
                tweetId.toString(),
                NoteCommand(
                    correlationId = UUID.randomUUID().toString(),
                    operation = NoteOperation.GET_BY_TWEET_ID,
                    tweetId = tweetId
                )
            )
        )
    }

    private fun requireNote(reply: NoteReply): NoteResponseTo {
        requireSuccess(reply)
        return reply.note?.toResponse()
            ?: throw RemoteDiscussionException(HttpStatus.INTERNAL_SERVER_ERROR, 50001, "Missing note in reply")
    }

    private fun requireNotes(reply: NoteReply): List<NoteResponseTo> {
        requireSuccess(reply)
        return reply.notes.map { it.toResponse() }
    }

    private fun requireSuccess(reply: NoteReply) {
        if (!reply.success) {
            throw RemoteDiscussionException(
                HttpStatus.valueOf(reply.httpStatus),
                reply.errorCode,
                reply.message ?: "Discussion request failed"
            )
        }
    }

    private fun normalizeSort(sort: Array<String>): List<String> =
        if (sort.size == 1 && sort[0].contains(',')) {
            sort[0].split(',').map(String::trim)
        } else {
            sort.toList()
        }

    private fun NotePayload.toResponse(): NoteResponseTo =
        NoteResponseTo(id = id, tweetId = tweetId, country = country, content = content)

    private fun generateId(): Long = abs(UUID.randomUUID().mostSignificantBits)
}
