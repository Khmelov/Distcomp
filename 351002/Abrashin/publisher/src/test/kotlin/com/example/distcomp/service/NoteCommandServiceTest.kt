package com.example.distcomp.service

import com.example.distcomp.data.dbo.NoteProjectionDbo
import com.example.distcomp.dto.request.NoteRequestTo
import com.example.distcomp.exception.NoteRequestTimeoutException
import com.example.distcomp.exception.NotFoundException
import com.example.distcomp.repository.TweetRepository
import com.example.notecontracts.NoteCommand
import com.example.notecontracts.NoteOperation
import com.example.notecontracts.NotePayload
import com.example.notecontracts.NoteReply
import com.example.notecontracts.NoteState
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class NoteCommandServiceTest {
    private val gateway = FakePublisherNoteGateway()
    private val projectionStore = FakeNoteProjectionStore()
    private val tweetRepository = Mockito.mock(TweetRepository::class.java)
    private val service = NoteCommandService(gateway, projectionStore, tweetRepository)

    @Test
    fun `create generates id marks pending and publishes create command`() {
        Mockito.`when`(tweetRepository.existsById(11L)).thenReturn(true)

        val result = service.create(NoteRequestTo(tweetId = 11, country = "BY", content = "hello"))

        assertEquals("11", gateway.sentKey)
        assertEquals(NoteOperation.CREATE, gateway.sentCommand?.operation)
        assertEquals(result.id, gateway.sentCommand?.noteId)
        assertEquals(result.id, projectionStore.lastPendingId)
        assertEquals(11L, projectionStore.lastPendingTweetId)
        assertNotNull(result.id)
        assertEquals("hello", result.content)
    }

    @Test
    fun `get by id resolves route and maps reply`() {
        projectionStore.routes[5L] = NoteProjectionDbo(id = 5, tweetId = 11, state = NoteState.APPROVE)
        gateway.reply = NoteReply(
            correlationId = "corr",
            operation = NoteOperation.GET_BY_ID,
            success = true,
            httpStatus = 200,
            note = NotePayload(id = 5, tweetId = 11, country = "BY", content = "hello", state = NoteState.APPROVE)
        )

        val result = service.getById(5)

        assertEquals("11", gateway.awaitKey)
        assertEquals(NoteOperation.GET_BY_ID, gateway.awaitCommand?.operation)
        assertEquals(5L, result.id)
        assertEquals(11L, result.tweetId)
    }

    @Test
    fun `put routes by stored tweet and sends new tweet in payload`() {
        Mockito.`when`(tweetRepository.existsById(22L)).thenReturn(true)
        projectionStore.routes[7L] = NoteProjectionDbo(id = 7, tweetId = 11, state = NoteState.APPROVE)
        gateway.reply = NoteReply(
            correlationId = "corr",
            operation = NoteOperation.PUT,
            success = true,
            httpStatus = 200,
            note = NotePayload(id = 7, tweetId = 22, country = "BY", content = "updated", state = NoteState.APPROVE)
        )

        val result = service.put(7, NoteRequestTo(tweetId = 22, country = "BY", content = "updated"))

        assertEquals("11", gateway.awaitKey)
        assertEquals(22L, gateway.awaitCommand?.tweetId)
        assertEquals("updated", gateway.awaitCommand?.content)
        assertEquals(22L, result.tweetId)
    }

    @Test
    fun `get by tweet id throws when tweet missing`() {
        Mockito.`when`(tweetRepository.existsById(9L)).thenReturn(false)

        assertThrows(NotFoundException::class.java) {
            service.getByTweetId(9)
        }
    }

    @Test
    fun `timeout is propagated from gateway`() {
        projectionStore.routes[5L] = NoteProjectionDbo(id = 5, tweetId = 11, state = NoteState.APPROVE)
        gateway.awaitException = NoteRequestTimeoutException("Timed out waiting for note response")

        assertThrows(NoteRequestTimeoutException::class.java) {
            service.getById(5)
        }
    }

    private class FakePublisherNoteGateway : PublisherNoteGateway {
        var sentKey: String? = null
        var sentCommand: NoteCommand? = null
        var awaitKey: String? = null
        var awaitCommand: NoteCommand? = null
        var reply: NoteReply? = null
        var awaitException: RuntimeException? = null

        override fun send(commandKey: String, command: NoteCommand) {
            sentKey = commandKey
            sentCommand = command
        }

        override fun sendAndAwait(commandKey: String, command: NoteCommand): NoteReply {
            awaitKey = commandKey
            awaitCommand = command
            awaitException?.let { throw it }
            return requireNotNull(reply)
        }
    }

    private class FakeNoteProjectionStore : NoteProjectionStore {
        val routes = mutableMapOf<Long, NoteProjectionDbo>()
        var lastPendingId: Long? = null
        var lastPendingTweetId: Long? = null

        override fun markPending(id: Long, tweetId: Long) {
            lastPendingId = id
            lastPendingTweetId = tweetId
            routes[id] = NoteProjectionDbo(id = id, tweetId = tweetId, state = NoteState.PENDING)
        }

        override fun requireRoute(id: Long): NoteProjectionDbo =
            routes[id] ?: throw NotFoundException("Note with id $id not found")

        override fun applyReply(reply: NoteReply) = Unit
    }
}
