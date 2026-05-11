package com.example.distcomp.service

import com.example.distcomp.cache.CacheSupport
import com.example.distcomp.data.dbo.NoteProjectionDbo
import com.example.distcomp.dto.request.NoteRequestTo
import com.example.distcomp.dto.response.NoteResponseTo
import com.example.notecontracts.NoteState
import java.time.Duration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class NoteServiceTest {
    private val noteCommandService = Mockito.mock(NoteCommandService::class.java)
    private val projectionStore = Mockito.mock(NoteProjectionStore::class.java)
    private val service = NoteService(
        noteCommandService = noteCommandService,
        projectionStore = projectionStore,
        cacheSupport = CacheSupport(null, Duration.ofMinutes(10))
    )

    @Test
    fun `get by id uses cache after first fetch`() {
        val response = NoteResponseTo(id = 7, tweetId = 11, country = "BY", content = "hello")
        Mockito.`when`(noteCommandService.getById(7L)).thenReturn(response)

        val first = service.getById(7)
        val second = service.getById(7)

        assertEquals(response, first)
        assertEquals(response, second)
        Mockito.verify(noteCommandService, Mockito.times(1)).getById(7L)
    }

    @Test
    fun `patch refreshes by id cache and evicts page and tweet caches`() {
        val oldRoute = NoteProjectionDbo(id = 5, tweetId = 11, state = NoteState.PENDING)
        Mockito.`when`(projectionStore.requireRoute(5L)).thenReturn(oldRoute)
        Mockito.`when`(noteCommandService.getAll(0, 10, arrayOf("id,desc"))).thenReturn(
            listOf(NoteResponseTo(id = 5, tweetId = 11, content = "before"))
        )
        Mockito.`when`(noteCommandService.getByTweetId(11L)).thenReturn(
            listOf(NoteResponseTo(id = 5, tweetId = 11, content = "before"))
        )
        Mockito.`when`(noteCommandService.getByTweetId(22L)).thenReturn(emptyList())

        service.getAll(0, 10, arrayOf("id,desc"))
        service.getNotesByTweetId(11)
        service.getNotesByTweetId(22)

        val patched = NoteResponseTo(id = 5, tweetId = 22, country = "BY", content = "after")
        Mockito.`when`(noteCommandService.patch(5L, NoteRequestTo(tweetId = 22, country = "BY", content = "after")))
            .thenReturn(patched)

        val result = service.patch(5, NoteRequestTo(tweetId = 22, country = "BY", content = "after"))

        assertEquals(patched, result)
        assertEquals(patched, service.getById(5))
        Mockito.verify(noteCommandService, Mockito.never()).getById(5L)

        service.getAll(0, 10, arrayOf("id,desc"))
        service.getNotesByTweetId(11)
        service.getNotesByTweetId(22)

        Mockito.verify(noteCommandService, Mockito.times(2)).getAll(0, 10, arrayOf("id,desc"))
        Mockito.verify(noteCommandService, Mockito.times(2)).getByTweetId(11L)
        Mockito.verify(noteCommandService, Mockito.times(2)).getByTweetId(22L)
    }

    @Test
    fun `delete evicts cached note and tweet collection`() {
        val route = NoteProjectionDbo(id = 9, tweetId = 44, state = NoteState.APPROVE)
        Mockito.`when`(projectionStore.requireRoute(9L)).thenReturn(route)
        Mockito.`when`(noteCommandService.getById(9L)).thenReturn(
            NoteResponseTo(id = 9, tweetId = 44, country = "BY", content = "cached")
        )
        Mockito.`when`(noteCommandService.getByTweetId(44L)).thenReturn(
            listOf(NoteResponseTo(id = 9, tweetId = 44, country = "BY", content = "cached"))
        )

        service.getById(9)
        service.getNotesByTweetId(44)
        service.delete(9)
        service.getById(9)
        service.getNotesByTweetId(44)

        Mockito.verify(noteCommandService, Mockito.times(2)).getById(9L)
        Mockito.verify(noteCommandService, Mockito.times(2)).getByTweetId(44L)
        Mockito.verify(noteCommandService).delete(9L)
    }
}
